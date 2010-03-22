/**
 * 
 */
package de.ovgu.cide.editor.inlineprojection;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentInformationMappingExtension;
import org.eclipse.jface.text.IDocumentListener;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ISlaveDocumentManager;
import org.eclipse.jface.text.ITextViewerExtension5;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.TextUtilities;
import org.eclipse.jface.text.projection.ProjectionDocument;
import org.eclipse.jface.text.projection.ProjectionDocumentEvent;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.AnnotationModelEvent;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension;
import org.eclipse.jface.text.source.IAnnotationModelListener;
import org.eclipse.jface.text.source.IAnnotationModelListenerExtension;
import org.eclipse.jface.text.source.IOverviewRuler;
import org.eclipse.jface.text.source.IVerticalRuler;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.IProjectionPosition;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.custom.ST;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import de.ovgu.cide.editor.ColoredTextEditor;

@SuppressWarnings(value = { "unchecked" })
public class InlineProjectionSourceViewer extends ProjectionViewer implements
		ITextViewerExtension5 {

	/**
	 * Internal listener to changes of the annotation model.
	 */
	private class AnnotationModelListener implements IAnnotationModelListener,
			IAnnotationModelListenerExtension {

		/*
		 * @see
		 * org.eclipse.jface.text.source.IAnnotationModelListener#modelChanged
		 * (org.eclipse.jface.text.source.IAnnotationModel)
		 */
		public void modelChanged(IAnnotationModel model) {
			processModelChanged(model, null);
		}

		/*
		 * @seeorg.eclipse.jface.text.source.IAnnotationModelListenerExtension#
		 * modelChanged(org.eclipse.jface.text.source.AnnotationModelEvent)
		 */
		public void modelChanged(AnnotationModelEvent event) {
			processModelChanged(event.getAnnotationModel(), event);
		}

		private void processModelChanged(IAnnotationModel model,
				AnnotationModelEvent event) {
			if (model == fInlineProjectionAnnotationModel) {

				if (fProjectionSummary != null)
					fProjectionSummary
							.updateSummaries(new NullProgressMonitor());
				processInlineCatchupRequest(event);

			} else if (model == getAnnotationModel()
					&& fProjectionSummary != null)
				fProjectionSummary.updateSummaries(new NullProgressMonitor());
		}
	}

	/**
	 * Executes the 'replaceVisibleDocument' operation when called the first
	 * time. Self-destructs afterwards.
	 */
	private class ReplaceVisibleDocumentExecutor implements IDocumentListener {

		private IDocument fSlaveDocument;

		private IDocument fExecutionTrigger;

		/**
		 * Creates a new executor in order to free the given slave document.
		 * 
		 * @param slaveDocument
		 *            the slave document to free
		 */
		public ReplaceVisibleDocumentExecutor(IDocument slaveDocument) {
			fSlaveDocument = slaveDocument;
		}

		/**
		 * Installs this executor on the given trigger document.
		 * 
		 * @param executionTrigger
		 *            the trigger document
		 */
		public void install(IDocument executionTrigger) {
			if (executionTrigger != null && fSlaveDocument != null) {
				fExecutionTrigger = executionTrigger;
				fExecutionTrigger.addDocumentListener(this);
			}
		}

		/*
		 * @see
		 * org.eclipse.jface.text.IDocumentListener#documentAboutToBeChanged
		 * (org.eclipse.jface.text.DocumentEvent)
		 */
		public void documentAboutToBeChanged(DocumentEvent event) {
		}

		/*
		 * @see
		 * org.eclipse.jface.text.IDocumentListener#documentChanged(org.eclipse
		 * .jface.text.DocumentEvent)
		 */
		public void documentChanged(DocumentEvent event) {
			fExecutionTrigger.removeDocumentListener(this);
			executeReplaceVisibleDocument(fSlaveDocument);
		}
	}

	/**
	 * A command representing a change of the projection document. This can be
	 * either adding a master document range, removing a master document change,
	 * or invalidating the viewer text presentation.
	 */
	private static class ProjectionCommand {

		final static int ADD = 0;

		final static int REMOVE = 1;

		final static int INVALIDATE_PRESENTATION = 2;

		ProjectionDocument fProjection;

		int fType;

		int fOffset;

		int fLength;

		ProjectionCommand(ProjectionDocument projection, int type, int offset,
				int length) {
			fProjection = projection;
			fType = type;
			fOffset = offset;
			fLength = length;
		}

		ProjectionCommand(int offset, int length) {
			fType = INVALIDATE_PRESENTATION;
			fOffset = offset;
			fLength = length;
		}

		int computeExpectedCosts() {

			switch (fType) {
			case ADD: {
				try {
					IRegion[] gaps = fProjection
							.computeUnprojectedMasterRegions(fOffset, fLength);
					return gaps == null ? 0 : gaps.length;
				} catch (BadLocationException x) {
				}
				break;
			}
			case REMOVE: {
				try {
					IRegion[] fragments = fProjection
							.computeProjectedMasterRegions(fOffset, fLength);
					return fragments == null ? 0 : fragments.length;
				} catch (BadLocationException x) {
				}
				break;
			}
			}
			return 0;
		}
	}

	/**
	 * The queue of projection command objects.
	 */
	private static class ProjectionCommandQueue {

		final static int REDRAW_COSTS = 15;

		final static int INVALIDATION_COSTS = 10;

		List fList = new ArrayList(15);

		int fExpectedExecutionCosts = -1;

		void add(ProjectionCommand command) {
			fList.add(command);
		}

		Iterator iterator() {
			return fList.iterator();
		}

		void clear() {
			fList.clear();
			fExpectedExecutionCosts = -1;
		}

		boolean passedRedrawCostsThreshold() {
			if (fExpectedExecutionCosts == -1)
				computeExpectedExecutionCosts();
			return fExpectedExecutionCosts > REDRAW_COSTS;
		}

		boolean passedInvalidationCostsThreshold() {
			if (fExpectedExecutionCosts == -1)
				computeExpectedExecutionCosts();
			return fExpectedExecutionCosts > INVALIDATION_COSTS;
		}

		private void computeExpectedExecutionCosts() {
			int max_costs = Math.max(REDRAW_COSTS, INVALIDATION_COSTS);
			fExpectedExecutionCosts = fList.size();
			if (fExpectedExecutionCosts <= max_costs) {
				ProjectionCommand command;
				Iterator e = fList.iterator();
				while (e.hasNext()) {
					command = (ProjectionCommand) e.next();
					fExpectedExecutionCosts += command.computeExpectedCosts();
					if (fExpectedExecutionCosts > max_costs)
						break;
				}
			}
		}
	}

	/** The projection annotation model used by this viewer. */
	private InlineProjectionAnnotationModel fInlineProjectionAnnotationModel;

	/** The annotation model listener */
	private IAnnotationModelListener fAnnotationModelListener = new AnnotationModelListener();

	/** The projection summary. */
	private InlineProjectionSummary fProjectionSummary;

	/** Indication that an annotation world change has not yet been processed. */
	private boolean fPendingAnnotationWorldChange = false;

	/**
	 * Indication whether projection changes in the visible document should be
	 * considered.
	 */
	private boolean fHandleInlineProjectionChanges = true;

	/** Internal lock for protecting the list of pending requests */
	private Object fLock = new Object();

	/** The list of pending requests */
	private List fPendingRequests = new ArrayList();

	/** The replace-visible-document execution trigger */
	private IDocument fReplaceVisibleDocumentExecutionTrigger;

	/**
	 * <code>true</code> if projection was on the last time we switched to
	 * segmented mode.
	 */
	private boolean fWasInlineProjectionEnabled;

	/**
	 * The queue of projection commands used to assess the costs of projection
	 * changes.
	 */
	private ProjectionCommandQueue fCommandQueue;

	/**
	 * The amount of lines deleted by the last document event issued by the
	 * visible document event.
	 * 
	 * @since 3.1
	 */
	private int fDeletedLines;

	private ProjectionAnnotationsCalculator projectionAnnotationCalculator;

	/**
	 * Creates a new projection source viewer.
	 * 
	 * @param parent
	 *            the SWT parent control
	 * @param ruler
	 *            the vertical ruler
	 * @param overviewRuler
	 *            the overview ruler
	 * @param showsAnnotationOverview
	 *            <code>true</code> if the overview ruler should be shown
	 * @param styles
	 *            the SWT style bits
	 * @param editor
	 * @param projectionAnnotationCalculator
	 * @param fPreferenceStore
	 */
	public InlineProjectionSourceViewer(Composite parent, IVerticalRuler ruler,
			IOverviewRuler overviewRuler, boolean showsAnnotationOverview,
			int styles, ColoredTextEditor editor) {
		super(parent, ruler, overviewRuler, showsAnnotationOverview, styles);
		this.projectionAnnotationCalculator = new ProjectionAnnotationsCalculator(
				editor);
	}

	public ProjectionAnnotationsCalculator getProjectionAnnotationCalculator() {
		return projectionAnnotationCalculator;
	}

	/**
	 * Sets the projection summary for this viewer.
	 * 
	 * @param projectionSummary
	 *            the projection summary.
	 */
	public void setProjectionSummary(InlineProjectionSummary projectionSummary) {
		fProjectionSummary = projectionSummary;
	}

	/**
	 * Adds the projection annotation model to the given annotation model.
	 * 
	 * @param model
	 *            the model to which the projection annotation model is added
	 */
	private void addProjectionAnnotationModel(IAnnotationModel model) {
		if (model instanceof IAnnotationModelExtension) {
			IAnnotationModelExtension extension = (IAnnotationModelExtension) model;
			extension.addAnnotationModel(
					InlineProjectionSupport.INLINEPROJECTION,
					fInlineProjectionAnnotationModel);
			model.addAnnotationModelListener(fAnnotationModelListener);
		}
	}

	/**
	 * Removes the projection annotation model from the given annotation model.
	 * 
	 * @param model
	 *            the mode from which the projection annotation model is removed
	 * @return the removed projection annotation model or <code>null</code> if
	 *         there was none
	 */
	private IAnnotationModel removeProjectionAnnotationModel(
			IAnnotationModel model) {
		if (model instanceof IAnnotationModelExtension) {
			model.removeAnnotationModelListener(fAnnotationModelListener);
			IAnnotationModelExtension extension = (IAnnotationModelExtension) model;
			return extension
					.removeAnnotationModel(InlineProjectionSupport.INLINEPROJECTION);
		}
		return null;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.source.SourceViewer#setDocument(org.eclipse.jface
	 * .text.IDocument, org.eclipse.jface.text.source.IAnnotationModel, int,
	 * int)
	 */
	public void setDocument(IDocument document,
			IAnnotationModel annotationModel, int modelRangeOffset,
			int modelRangeLength) {

		super.setDocument(document, annotationModel, modelRangeOffset,
				modelRangeLength);

		boolean wasProjectionEnabled = false;

		synchronized (fLock) {
			fPendingRequests.clear();
		}

		if (fInlineProjectionAnnotationModel != null) {
			wasProjectionEnabled = removeProjectionAnnotationModel(getVisualAnnotationModel()) != null;
			fInlineProjectionAnnotationModel = null;
		}

		super.setDocument(document, annotationModel, modelRangeOffset,
				modelRangeLength);

		if (wasProjectionEnabled && document != null)
			enableInlineProjection();

		if (wasProjectionEnabled && document != null)
			projectionAnnotationCalculator.calculateProjectionAnnotations();

	}

	/*
	 * @see
	 * org.eclipse.jface.text.source.SourceViewer#createVisualAnnotationModel
	 * (org.eclipse.jface.text.source.IAnnotationModel)
	 */
	protected IAnnotationModel createVisualAnnotationModel(
			IAnnotationModel annotationModel) {
		super.createVisualAnnotationModel(annotationModel);
		IAnnotationModel model = super
				.createVisualAnnotationModel(annotationModel);
		fInlineProjectionAnnotationModel = new InlineProjectionAnnotationModel();
		return model;
	}

	/**
	 * Returns the projection annotation model.
	 * 
	 * @return the projection annotation model
	 */
	public InlineProjectionAnnotationModel getInlineProjectionAnnotationModel() {
		IAnnotationModel model = getVisualAnnotationModel();
		if (model instanceof IAnnotationModelExtension) {
			IAnnotationModelExtension extension = (IAnnotationModelExtension) model;
			return (InlineProjectionAnnotationModel) extension
					.getAnnotationModel(InlineProjectionSupport.INLINEPROJECTION);
		}
		return null;
	}

	/**
	 * Returns whether this viewer is in projection mode.
	 * 
	 * @return <code>true</code> if this viewer is in projection mode,
	 *         <code>false</code> otherwise
	 */
	public final boolean isInlineProjectionMode() {
		return getInlineProjectionAnnotationModel() != null;
	}

	/**
	 * Disables the projection mode.
	 */
	public final void disableInlineProjection() {
		if (isInlineProjectionMode()) {
			removeProjectionAnnotationModel(getVisualAnnotationModel());
			fInlineProjectionAnnotationModel.removeAllAnnotations();
			fFindReplaceDocumentAdapter = null;
			fireInlineProjectionDisabled();
		}
	}

	/**
	 * Enables the projection mode.
	 */
	public final void enableInlineProjection() {
		if (!isInlineProjectionMode()) {
			addProjectionAnnotationModel(getVisualAnnotationModel());
			fFindReplaceDocumentAdapter = null;
			fireInlineProjectionEnabled();
		}
	}

	private void expandAll() {
		int offset = 0;
		IDocument doc = getDocument();
		int length = doc == null ? 0 : doc.getLength();
		if (isInlineProjectionMode()) {
			fInlineProjectionAnnotationModel.expandAll(offset, length);
		}
	}

	private void expand() {
		if (isInlineProjectionMode()) {
			Position found = null;
			Annotation bestMatch = null;
			Point selection = getSelectedRange();
			for (Iterator e = fInlineProjectionAnnotationModel
					.getAnnotationIterator(); e.hasNext();) {
				InlineProjectionAnnotation annotation = (InlineProjectionAnnotation) e
						.next();
				if (annotation.isCollapsed()) {
					Position position = fInlineProjectionAnnotationModel
							.getPosition(annotation);
					// take the first most fine grained match
					if (position != null && touches(selection, position))
						if (found == null
								|| position.includes(found.offset)
								&& position.includes(found.offset
										+ found.length)) {
							found = position;
							bestMatch = annotation;
						}
				}
			}

			if (bestMatch != null) {
				fInlineProjectionAnnotationModel.expand(bestMatch);
				revealRange(selection.x, selection.y);
			}
		}
	}

	private boolean touches(Point selection, Position position) {
		return position.overlapsWith(selection.x, selection.y)
				|| selection.y == 0
				&& position.offset + position.length == selection.x
						+ selection.y;
	}

	private void collapse() {
		if (isInlineProjectionMode()) {
			Position found = null;
			Annotation bestMatch = null;
			Point selection = getSelectedRange();
			for (Iterator e = fInlineProjectionAnnotationModel
					.getAnnotationIterator(); e.hasNext();) {
				InlineProjectionAnnotation annotation = (InlineProjectionAnnotation) e
						.next();
				if (!annotation.isCollapsed()) {
					Position position = fInlineProjectionAnnotationModel
							.getPosition(annotation);
					// take the first most fine grained match
					if (position != null && touches(selection, position))
						if (found == null
								|| found.includes(position.offset)
								&& found.includes(position.offset
										+ position.length)) {
							found = position;
							bestMatch = annotation;
						}
				}
			}

			if (bestMatch != null) {
				fInlineProjectionAnnotationModel.collapse(bestMatch);
				revealRange(selection.x, selection.y);
			}
		}
	}

	/*
	 * @since 3.2
	 */
	private void collapseAll() {
		int offset = 0;
		IDocument doc = getDocument();
		int length = doc == null ? 0 : doc.getLength();
		if (isInlineProjectionMode()) {
			fInlineProjectionAnnotationModel.collapseAll(offset, length);
		}
	}

	/**
	 * Adds the given master range to the given projection document. While the
	 * modification is processed, the viewer no longer handles projection
	 * changes, as it is causing them.
	 * 
	 * @param projection
	 *            the projection document
	 * @param offset
	 *            the offset in the master document
	 * @param length
	 *            the length in the master document
	 * @throws BadLocationException
	 *             in case the specified range is invalid
	 * 
	 * @see ProjectionDocument#addMasterDocumentRange(int, int)
	 */
	private void addMasterDocumentRange(ProjectionDocument projection,
			int offset, int length) throws BadLocationException {

		if (fCommandQueue != null) {
			fCommandQueue.add(new ProjectionCommand(projection,
					ProjectionCommand.ADD, offset, length));
		} else {
			try {
				fHandleInlineProjectionChanges = false;
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=108258
				// make sure the document range is strictly line based
				// int end = offset + length;
				// offset = toLineStart(projection.getMasterDocument(), offset,
				// false);
				// length = toLineStart(projection.getMasterDocument(), end,
				// true)
				// - offset;
				projection.addMasterDocumentRange(offset + 1, length - 1);
			} finally {
				fHandleInlineProjectionChanges = true;
			}
		}
	}

	/**
	 * Removes the given master range from the given projection document. While
	 * the modification is processed, the viewer no longer handles projection
	 * changes, as it is causing them.
	 * 
	 * @param projection
	 *            the projection document
	 * @param offset
	 *            the offset in the master document
	 * @param length
	 *            the length in the master document
	 * @throws BadLocationException
	 *             in case the specified range is invalid
	 * 
	 * @see ProjectionDocument#removeMasterDocumentRange(int, int)
	 */
	private void removeMasterDocumentRange(ProjectionDocument projection,
			int offset, int length) throws BadLocationException {
		if (fCommandQueue != null) {
			fCommandQueue.add(new ProjectionCommand(projection,
					ProjectionCommand.REMOVE, offset, length));
		} else {
			try {
				fHandleInlineProjectionChanges = false;
				// https://bugs.eclipse.org/bugs/show_bug.cgi?id=108258
				// make sure the document range is strictly line based
				// int end = offset + length;
				// offset = toLineStart(projection.getMasterDocument(), offset,
				// false);
				// length = toLineStart(projection.getMasterDocument(), end,
				// true)
				// - offset;
				projection.removeMasterDocumentRange(offset + 1, length - 1);
			} finally {
				fHandleInlineProjectionChanges = true;
			}
		}
	}

	/**
	 * Returns the first line offset &lt;= <code>offset</code>. If
	 * <code>testLastLine</code> is <code>true</code> and the offset is on last
	 * line then <code>offset</code> is returned.
	 * 
	 * @param document
	 *            the document
	 * @param offset
	 *            the master document offset
	 * @param testLastLine
	 *            <code>true</code> if the test for the last line should be
	 *            performed
	 * @return the closest line offset &gt;= <code>offset</code>
	 * @throws BadLocationException
	 *             if the offset is invalid
	 * @since 3.2
	 */
	// private int toLineStart(IDocument document, int offset, boolean
	// testLastLine)
	// throws BadLocationException {
	// if (document == null)
	// return offset;
	//
	// if (testLastLine
	// && offset >= document.getLineInformationOfOffset(
	// document.getLength() - 1).getOffset())
	// return offset;
	//
	// return document.getLineInformationOfOffset(offset).getOffset();
	// }
	/*
	 * @see org.eclipse.jface.text.TextViewer#setVisibleRegion(int, int)
	 */
	public void setVisibleRegion(int start, int length) {
		if (!isSegmented())
			fWasInlineProjectionEnabled = isInlineProjectionMode();
		disableProjection();
		super.setVisibleRegion(start, length);
	}

	/*
	 * @see
	 * org.eclipse.jface.text.TextViewer#setVisibleDocument(org.eclipse.jface
	 * .text.IDocument)
	 */
	protected void setVisibleDocument(IDocument document) {
		if (!isInlineProjectionMode()) {
			super.setVisibleDocument(document);
			return;
		}

		// In projection mode we don't want to throw away the find/replace
		// document adapter
		FindReplaceDocumentAdapter adapter = fFindReplaceDocumentAdapter;
		super.setVisibleDocument(document);
		fFindReplaceDocumentAdapter = adapter;
	}

	/*
	 * @see org.eclipse.jface.text.TextViewer#resetVisibleRegion()
	 */
	public void resetVisibleRegion() {
		super.resetVisibleRegion();
		if (fWasInlineProjectionEnabled)
			enableInlineProjection();
	}

	/*
	 * @see org.eclipse.jface.text.ITextViewer#getVisibleRegion()
	 */
	public IRegion getVisibleRegion() {
		disableProjection();
		IRegion visibleRegion = getModelCoverage();
		if (visibleRegion == null)
			visibleRegion = new Region(0, 0);

		return visibleRegion;
	}

	/*
	 * @see
	 * org.eclipse.jface.text.ITextViewer#overlapsWithVisibleRegion(int,int)
	 */
	public boolean overlapsWithVisibleRegion(int offset, int length) {
		disableProjection();
		IRegion coverage = getModelCoverage();
		if (coverage == null)
			return false;

		boolean appending = (offset == coverage.getOffset()
				+ coverage.getLength())
				&& length == 0;
		return appending
				|| TextUtilities.overlaps(coverage, new Region(offset, length));
	}

	/**
	 * Replace the visible document with the given document. Maintains the
	 * scroll offset and the selection.
	 * 
	 * @param slave
	 *            the visible document
	 */
	private void replaceVisibleDocument(IDocument slave) {
		if (fReplaceVisibleDocumentExecutionTrigger != null) {
			ReplaceVisibleDocumentExecutor executor = new ReplaceVisibleDocumentExecutor(
					slave);
			executor.install(fReplaceVisibleDocumentExecutionTrigger);
		} else
			executeReplaceVisibleDocument(slave);
	}

	private void executeReplaceVisibleDocument(IDocument visibleDocument) {
		StyledText textWidget = getTextWidget();
		try {
			if (textWidget != null && !textWidget.isDisposed())
				textWidget.setRedraw(false);

			int topIndex = getTopIndex();
			Point selection = getSelectedRange();
			setVisibleDocument(visibleDocument);
			Point currentSelection = getSelectedRange();
			if (currentSelection.x != selection.x
					|| currentSelection.y != selection.y)
				setSelectedRange(selection.x, selection.y);
			setTopIndex(topIndex);

		} finally {
			if (textWidget != null && !textWidget.isDisposed())
				textWidget.setRedraw(true);
		}
	}

	/**
	 * Hides the given range by collapsing it. If requested, a redraw request is
	 * issued.
	 * 
	 * @param offset
	 *            the offset of the range to hide
	 * @param length
	 *            the length of the range to hide
	 * @param fireRedraw
	 *            <code>true</code> if a redraw request should be issued,
	 *            <code>false</code> otherwise
	 * @throws BadLocationException
	 *             in case the range is invalid
	 */
	private void collapse(int offset, int length, boolean fireRedraw)
			throws BadLocationException {
		ProjectionDocument projection = null;

		IDocument visibleDocument = getVisibleDocument();
		if (visibleDocument instanceof ProjectionDocument)
			projection = (ProjectionDocument) visibleDocument;
		else {
			IDocument master = getDocument();
			IDocument slave = createSlaveDocument(getDocument());
			if (slave instanceof ProjectionDocument) {
				projection = (ProjectionDocument) slave;
				addMasterDocumentRange(projection, 0, master.getLength());
				replaceVisibleDocument(projection);
			}
		}

		if (projection != null)
			removeMasterDocumentRange(projection, offset, length);

		if (projection != null && fireRedraw) {
			// repaint line above to get the folding box
			IDocument document = getDocument();
			int line = document.getLineOfOffset(offset);
			if (line > 0) {
				IRegion info = document.getLineInformation(line - 1);
				internalInvalidateTextPresentation(info.getOffset(), info
						.getLength());
			}
		}
	}

	/**
	 * Makes the given range visible again while not changing the folding state
	 * of any contained ranges. If requested, a redraw request is issued.
	 * 
	 * @param offset
	 *            the offset of the range to be expanded
	 * @param length
	 *            the length of the range to be expanded
	 * @param fireRedraw
	 *            <code>true</code> if a redraw request should be issued,
	 *            <code>false</code> otherwise
	 * @throws BadLocationException
	 *             in case the range is invalid
	 */
	private void expand(int offset, int length, boolean fireRedraw)
			throws BadLocationException {
		IDocument slave = getVisibleDocument();
		if (slave instanceof ProjectionDocument) {
			ProjectionDocument projection = (ProjectionDocument) slave;

			// expand
			addMasterDocumentRange(projection, offset, length);

			// collapse contained regions
			InlineProjectionAnnotation[] collapsed = computeCollapsedNestedAnnotations(
					offset, length);
			if (collapsed != null) {
				for (int i = 0; i < collapsed.length; i++) {
					IRegion[] regions = computeCollapsedRegions(fInlineProjectionAnnotationModel
							.getPosition(collapsed[i]));
					if (regions != null)
						for (int j = 0; j < regions.length; j++)
							removeMasterDocumentRange(projection, regions[j]
									.getOffset(), regions[j].getLength());
				}
			}

			// redraw if requested
			if (fireRedraw)
				internalInvalidateTextPresentation(offset, length);
		}
	}

	/**
	 * Processes the request for catch up with the annotation model in the UI
	 * thread. If the current thread is not the UI thread or there are pending
	 * catch up requests, a new request is posted.
	 * 
	 * @param event
	 *            the annotation model event
	 */
	protected final void processInlineCatchupRequest(AnnotationModelEvent event) {
		if (Display.getCurrent() != null) {
			boolean run = false;
			synchronized (fLock) {
				run = fPendingRequests.isEmpty();
			}
			if (run) {

				try {
					catchupWithProjectionAnnotationModel(event);
				} catch (BadLocationException x) {
					throw new IllegalArgumentException();
				}

			} else
				postInlineCatchupRequest(event);
		} else {
			postInlineCatchupRequest(event);
		}
	}

	/**
	 * Posts the request for catch up with the annotation model into the UI
	 * thread.
	 * 
	 * @param event
	 *            the annotation model event
	 */
	protected final void postInlineCatchupRequest(
			final AnnotationModelEvent event) {
		synchronized (fLock) {
			fPendingRequests.add(event);
			if (fPendingRequests.size() == 1) {
				StyledText widget = getTextWidget();
				if (widget != null) {
					Display display = widget.getDisplay();
					if (display != null) {
						display.asyncExec(new Runnable() {
							public void run() {
								try {
									while (true) {
										AnnotationModelEvent ame = null;
										synchronized (fLock) {
											if (fPendingRequests.size() == 0)
												return;
											ame = (AnnotationModelEvent) fPendingRequests
													.remove(0);
										}
										catchupWithProjectionAnnotationModel(ame);
									}
								} catch (BadLocationException x) {
									try {
										catchupWithProjectionAnnotationModel(null);
									} catch (BadLocationException x1) {
										throw new IllegalArgumentException();
									} finally {
										synchronized (fLock) {
											fPendingRequests.clear();
										}
									}
								}
							}
						});
					}
				}
			}
		}
	}

	/**
	 * Tests whether the visible document's master document is identical to this
	 * viewer's document.
	 * 
	 * @return <code>true</code> if the visible document's master is identical
	 *         to this viewer's document
	 * @since 3.1
	 */
	private boolean isVisibleMasterDocumentSameAsDocument() {
		IDocument visibleDocument = getVisibleDocument();
		return (visibleDocument instanceof ProjectionDocument)
				&& ((ProjectionDocument) visibleDocument).getMasterDocument() == getDocument();
	}

	/**
	 * Adapts the slave visual document of this viewer to the changes described
	 * in the annotation model event. When the event is <code>null</code>, this
	 * is identical to a world change event.
	 * 
	 * @param event
	 *            the annotation model event or <code>null</code>
	 * @exception BadLocationException
	 *                in case the annotation model event is no longer in
	 *                synchronization with the document
	 */
	private void catchupWithProjectionAnnotationModel(AnnotationModelEvent event)
			throws BadLocationException {

		if (event == null || !isVisibleMasterDocumentSameAsDocument()) {

			fPendingAnnotationWorldChange = false;
			reinitializeInlineProjection();

		} else if (event.isWorldChange()) {

			if (event.isValid()) {
				fPendingAnnotationWorldChange = false;
				reinitializeInlineProjection();
			} else
				fPendingAnnotationWorldChange = true;

		} else if (fPendingAnnotationWorldChange) {
			if (event.isValid()) {
				fPendingAnnotationWorldChange = false;
				reinitializeInlineProjection();
			}
		} else {

			Annotation[] addedAnnotations = event.getAddedAnnotations();
			Annotation[] changedAnnotation = event.getChangedAnnotations();
			Annotation[] removedAnnotations = event.getRemovedAnnotations();

			fCommandQueue = new ProjectionCommandQueue();

			boolean isRedrawing = redraws();
			int topIndex = isRedrawing ? getTopIndex() : -1;

			processDeletions(event, removedAnnotations, true);
			List coverage = new ArrayList();
			processChanges(addedAnnotations, true, coverage);
			processChanges(changedAnnotation, true, coverage);

			ProjectionCommandQueue commandQueue = fCommandQueue;
			fCommandQueue = null;

			if (commandQueue.passedRedrawCostsThreshold()) {
				setRedraw(false);
				try {
					executeProjectionCommands(commandQueue, false);
				} catch (IllegalArgumentException x) {
					reinitializeInlineProjection();
				} finally {
					setRedraw(true, topIndex);
				}

			} else {

				StyledText textWidget = getTextWidget();

				try {
					if (isRedrawing && textWidget != null
							&& !textWidget.isDisposed())
						textWidget.setRedraw(false);

					boolean fireRedraw = !commandQueue
							.passedInvalidationCostsThreshold();
					executeProjectionCommands(commandQueue, fireRedraw);
					if (!fireRedraw)
						invalidateTextPresentation();
				} catch (IllegalArgumentException x) {
					reinitializeInlineProjection();
				} finally {
					if (isRedrawing && textWidget != null
							&& !textWidget.isDisposed())
						textWidget.setRedraw(true);
				}
			}
		}
	}

	private void executeProjectionCommands(ProjectionCommandQueue commandQueue,
			boolean fireRedraw) throws BadLocationException {

		ProjectionCommand command;
		Iterator e = commandQueue.iterator();
		while (e.hasNext()) {
			command = (ProjectionCommand) e.next();
			switch (command.fType) {
			case ProjectionCommand.ADD:
				addMasterDocumentRange(command.fProjection, command.fOffset,
						command.fLength);
				break;
			case ProjectionCommand.REMOVE:
				removeMasterDocumentRange(command.fProjection, command.fOffset,
						command.fLength);
				break;
			case ProjectionCommand.INVALIDATE_PRESENTATION:
				if (fireRedraw)
					invalidateTextPresentation(command.fOffset, command.fLength);
				break;
			}
		}

		commandQueue.clear();
	}

	private boolean covers(int offset, int length, Position position) {
		if (!(position.offset == offset && position.length == length)
				&& !position.isDeleted())
			return offset <= position.getOffset()
					&& position.getOffset() + position.getLength() <= offset
							+ length;
		return false;
	}

	private InlineProjectionAnnotation[] computeCollapsedNestedAnnotations(
			int offset, int length) {
		List annotations = new ArrayList(5);
		Iterator e = fInlineProjectionAnnotationModel.getAnnotationIterator();
		while (e.hasNext()) {
			InlineProjectionAnnotation annotation = (InlineProjectionAnnotation) e
					.next();
			if (annotation.isCollapsed()) {
				Position position = fInlineProjectionAnnotationModel
						.getPosition(annotation);
				if (position == null) {
					// annotation might already be deleted, we will be informed
					// later on about this deletion
					continue;
				}
				if (covers(offset, length, position))
					annotations.add(annotation);
			}
		}

		if (annotations.size() > 0) {
			InlineProjectionAnnotation[] result = new InlineProjectionAnnotation[annotations
					.size()];
			annotations.toArray(result);
			return result;
		}

		return null;
	}

	private void internalInvalidateTextPresentation(int offset, int length) {
		if (fCommandQueue != null) {
			fCommandQueue.add(new ProjectionCommand(offset, length));
		} else {
			invalidateTextPresentation(offset, length);
		}
	}

	/*
	 * We pass the removed annotation into this method for performance reasons
	 * only. Otherwise, they could be fetch from the event.
	 */
	private void processDeletions(AnnotationModelEvent event,
			Annotation[] removedAnnotations, boolean fireRedraw)
			throws BadLocationException {
		for (int i = 0; i < removedAnnotations.length; i++) {
			InlineProjectionAnnotation annotation = (InlineProjectionAnnotation) removedAnnotations[i];
			if (annotation.isCollapsed()) {
				Position expanded = event
						.getPositionOfRemovedAnnotation(annotation);
				expand(expanded.getOffset(), expanded.getLength(), fireRedraw);
			}
		}
	}

	/**
	 * Computes the region that must be collapsed when the given position is the
	 * position of an expanded projection annotation.
	 * 
	 * @param position
	 *            the position
	 * @return the range that must be collapsed
	 */
	public IRegion computeCollapsedRegion(Position position) {
		try {
			IDocument document = getDocument();
			if (document == null)
				return null;

			int line = document.getLineOfOffset(position.getOffset());
			int offset = document.getLineOffset(line + 1);

			int length = position.getLength() - (offset - position.getOffset());
			if (length > 0)
				return new Region(offset, length);
		} catch (BadLocationException x) {
		}

		return null;
	}

	/**
	 * Computes the regions that must be collapsed when the given position is
	 * the position of an expanded projection annotation.
	 * 
	 * @param position
	 *            the position
	 * @return the ranges that must be collapsed, or <code>null</code> if there
	 *         are none
	 * @since 3.1
	 */
	IRegion[] computeCollapsedRegions(Position position) {
		try {
			IDocument document = getDocument();
			if (document == null)
				return null;

			if (position instanceof IProjectionPosition) {
				IProjectionPosition projPosition = (IProjectionPosition) position;
				return projPosition.computeProjectionRegions(document);
			}

			return new IRegion[] { new Region(position.getOffset(), position
					.getLength()) };

			// int line = document.getLineOfOffset(position.getOffset());
			// int offset = document.getLineOffset(line + 1);
			//
			// int length = position.getLength() - (offset -
			// position.getOffset());
			// if (length > 0)
			// return new IRegion[] { new Region(offset, length) };
			//
			// return null;
		} catch (BadLocationException x) {
			return null;
		}
	}

	/**
	 * Computes the collapsed region anchor for the given position. Assuming
	 * that the position is the position of an expanded projection annotation,
	 * the anchor is the region that is still visible after the projection
	 * annotation has been collapsed.
	 * 
	 * @param position
	 *            the position
	 * @return the collapsed region anchor
	 */
	public Position computeCollapsedRegionAnchor(Position position) {
		try {
			IDocument document = getDocument();
			if (document == null)
				return null;

			int captionOffset = position.getOffset();
			if (position instanceof IProjectionPosition)
				captionOffset += ((IProjectionPosition) position)
						.computeCaptionOffset(document);

			IRegion lineInfo = document
					.getLineInformationOfOffset(captionOffset);
			return new Position(lineInfo.getOffset() + lineInfo.getLength(), 0);
		} catch (BadLocationException x) {
		}
		return null;
	}

	private void processChanges(Annotation[] annotations, boolean fireRedraw,
			List coverage) throws BadLocationException {
		for (int i = 0; i < annotations.length; i++) {
			InlineProjectionAnnotation annotation = (InlineProjectionAnnotation) annotations[i];
			Position position = fInlineProjectionAnnotationModel
					.getPosition(annotation);

			if (position == null)
				continue;

			if (!covers(coverage, position)) {
				if (annotation.isCollapsed()) {
					coverage.add(position);
					IRegion[] regions = computeCollapsedRegions(position);
					if (regions != null)
						for (int j = 0; j < regions.length; j++)
							collapse(regions[j].getOffset(), regions[j]
									.getLength(), fireRedraw);
				} else {
					expand(position.getOffset(), position.getLength(),
							fireRedraw);
				}
			}
		}
	}

	private boolean covers(List coverage, Position position) {
		Iterator e = coverage.iterator();
		while (e.hasNext()) {
			Position p = (Position) e.next();
			if (p.getOffset() <= position.getOffset()
					&& position.getOffset() + position.getLength() <= p
							.getOffset()
							+ p.getLength())
				return true;
		}
		return false;
	}

	/**
	 * Forces this viewer to throw away any old state and to initialize its
	 * content from its projection annotation model.
	 * 
	 * @throws BadLocationException
	 *             in case something goes wrong during initialization
	 */
	public final void reinitializeInlineProjection()
			throws BadLocationException {

		ProjectionDocument projection = null;

		ISlaveDocumentManager manager = getSlaveDocumentManager();
		if (manager != null) {
			IDocument master = getDocument();
			if (master != null) {
				IDocument slave = manager.createSlaveDocument(master);
				if (slave instanceof ProjectionDocument) {
					projection = (ProjectionDocument) slave;
					addMasterDocumentRange(projection, 0, master.getLength());
				}
			}
		}

		if (projection != null) {
			Iterator e = fInlineProjectionAnnotationModel
					.getAnnotationIterator();
			while (e.hasNext()) {
				InlineProjectionAnnotation annotation = (InlineProjectionAnnotation) e
						.next();
				if (annotation.isCollapsed()) {
					Position position = fInlineProjectionAnnotationModel
							.getPosition(annotation);
					if (position != null) {
						IRegion[] regions = computeCollapsedRegions(position);
						if (regions != null)
							for (int i = 0; i < regions.length; i++)
								removeMasterDocumentRange(projection,
										regions[i].getOffset(), regions[i]
												.getLength());
					}
				}
			}

		}

		replaceVisibleDocument(projection);
	}

	// /*
	// * @see
	// org.eclipse.jface.text.TextViewer#handleVerifyEvent(org.eclipse.swt.events.VerifyEvent)
	// */
	// protected void handleVerifyEvent(VerifyEvent e) {
	// IRegion modelRange = event2ModelRange(e);
	// if (exposeModelRange(modelRange))
	// e.doit = false;
	// else
	// super.handleVerifyEvent(e);
	// }

	// /**
	// * Adds the give column as last column to this viewer's vertical ruler.
	// *
	// * @param column
	// * the column to be added
	// */
	// public void addVerticalRulerColumn(IVerticalRulerColumn column) {
	// IVerticalRuler ruler = getVerticalRuler();
	// if (ruler instanceof CompositeRuler) {
	// CompositeRuler compositeRuler = (CompositeRuler) ruler;
	// compositeRuler.addDecorator(99, column);
	// }
	// }
	//
	// /**
	// * Removes the give column from this viewer's vertical ruler.
	// *
	// * @param column
	// * the column to be removed
	// */
	// public void removeVerticalRulerColumn(IVerticalRulerColumn column) {
	// IVerticalRuler ruler = getVerticalRuler();
	// if (ruler instanceof CompositeRuler) {
	// CompositeRuler compositeRuler = (CompositeRuler) ruler;
	// compositeRuler.removeDecorator(column);
	// }
	// }
	//
	// /*
	// * @see
	// org.eclipse.jface.text.ITextViewerExtension5#exposeModelRange(org.eclipse.jface.text.IRegion)
	// */
	// public boolean exposeModelRange(IRegion modelRange) {
	// if (isInlineProjectionMode())
	// return fInlineProjectionAnnotationModel.expandAll(modelRange.getOffset(),
	// modelRange.getLength());
	//
	// if (!overlapsWithVisibleRegion(modelRange.getOffset(), modelRange
	// .getLength())) {
	// resetVisibleRegion();
	// return true;
	// }
	//
	// return false;
	// }
	//
	// /*
	// * @see org.eclipse.jface.text.source.SourceViewer#setRangeIndication(int,
	// * int, boolean)
	// */
	// public void setRangeIndication(int offset, int length, boolean
	// moveCursor) {
	//
	// if (getRangeIndication() != null) {
	// List expand = new ArrayList(2);
	// if (moveCursor && fInlineProjectionAnnotationModel != null) {
	//
	// // expand the immediate effected collapsed regions
	// Iterator iterator = fInlineProjectionAnnotationModel
	// .getAnnotationIterator();
	// while (iterator.hasNext()) {
	// InlineProjectionAnnotation annotation = (InlineProjectionAnnotation)
	// iterator
	// .next();
	// if (annotation.isCollapsed()
	// && willAutoExpand(fInlineProjectionAnnotationModel
	// .getPosition(annotation), offset, length))
	// expand.add(annotation);
	// }
	//
	// if (!expand.isEmpty()) {
	// Iterator e = expand.iterator();
	// while (e.hasNext())
	// fInlineProjectionAnnotationModel
	// .expand((Annotation) e.next());
	// }
	// }
	// }
	//
	// super.setRangeIndication(offset, length, moveCursor);
	// }

	// private boolean willAutoExpand(Position position, int offset, int length)
	// {
	// if (position == null || position.isDeleted())
	// return false;
	// // right or left boundary
	// if (position.getOffset() == offset
	// || position.getOffset() + position.getLength() == offset
	// + length)
	// return true;
	// // completely embedded in given position
	// if (position.getOffset() < offset
	// && offset + length < position.getOffset()
	// + position.getLength())
	// return true;
	// return false;
	// }

	/*
	 * @see org.eclipse.jface.text.source.SourceViewer#handleDispose()
	 * 
	 * @since 3.0
	 */
	protected void handleDispose() {
		fWasInlineProjectionEnabled = false;
		super.handleDispose();
	}

	/*
	 * @see
	 * org.eclipse.jface.text.TextViewer#handleVisibleDocumentAboutToBeChanged
	 * (org.eclipse.jface.text.DocumentEvent)
	 */
	protected void handleVisibleDocumentChanged(DocumentEvent event) {
		if (fHandleInlineProjectionChanges
				&& event instanceof ProjectionDocumentEvent
				&& isInlineProjectionMode()) {
			ProjectionDocumentEvent e = (ProjectionDocumentEvent) event;

			DocumentEvent master = e.getMasterEvent();
			if (master != null)
				fReplaceVisibleDocumentExecutionTrigger = master.getDocument();

			try {

				int replaceLength = e.getText() == null ? 0 : e.getText()
						.length();
				if (ProjectionDocumentEvent.PROJECTION_CHANGE == e
						.getChangeType()) {
					if (e.getLength() == 0 && replaceLength != 0)
						fInlineProjectionAnnotationModel.expandAll(e
								.getMasterOffset(), e.getMasterLength());
				} else if (master != null
						&& (replaceLength > 0 || fDeletedLines > 1)) {
					try {
						int numberOfLines = e.getDocument().getNumberOfLines(
								e.getOffset(), replaceLength);
						if (numberOfLines > 1 || fDeletedLines > 1)
							fInlineProjectionAnnotationModel.expandAll(master
									.getOffset(), master.getLength());
					} catch (BadLocationException x) {
					}
				}

			} finally {
				fReplaceVisibleDocumentExecutionTrigger = null;
			}

		}
	}

	/*
	 * @see
	 * org.eclipse.jface.text.TextViewer#handleVisibleDocumentAboutToBeChanged
	 * (org.eclipse.jface.text.DocumentEvent)
	 * 
	 * @since 3.1
	 */
	protected void handleVisibleDocumentAboutToBeChanged(DocumentEvent event) {
		if (fHandleInlineProjectionChanges
				&& event instanceof ProjectionDocumentEvent
				&& isInlineProjectionMode()) {
			int deletedLines;
			try {
				deletedLines = event.getDocument().getNumberOfLines(
						event.getOffset(), event.getLength());
			} catch (BadLocationException e1) {
				deletedLines = 0;
			}
			fDeletedLines = deletedLines;
		}
	}

	/*
	 * @see
	 * org.eclipse.jface.text.ITextViewerExtension5#getCoveredModelRanges(org
	 * .eclipse.jface.text.IRegion)
	 */
	public IRegion[] getCoveredModelRanges(IRegion modelRange) {
		if (fInformationMapping == null)
			return new IRegion[] { new Region(modelRange.getOffset(),
					modelRange.getLength()) };

		if (fInformationMapping instanceof IDocumentInformationMappingExtension) {
			IDocumentInformationMappingExtension extension = (IDocumentInformationMappingExtension) fInformationMapping;
			try {
				return extension.getExactCoverage(modelRange);
			} catch (BadLocationException x) {
			}
		}

		return null;
	}

	/*
	 * @see org.eclipse.jface.text.ITextOperationTarget#doOperation(int)
	 */
	public void doOperation(int operation) {
		switch (operation) {
		case TOGGLE:
			if (canDoOperation(TOGGLE)) {
				if (!isInlineProjectionMode()) {
					enableInlineProjection();
				} else {
					expandAll();
					disableInlineProjection();
				}
				return;
			}
		}

		if (!isInlineProjectionMode()) {
			super.doOperation(operation);
			return;
		}

		StyledText textWidget = getTextWidget();
		if (textWidget == null)
			return;

		Point selection = null;
		switch (operation) {

		case CUT:

			if (redraws()) {
				selection = getSelectedRange();
				if (selection.y == 0)
					copyMarkedRegion(true);
				else
					copyToClipboard(selection.x, selection.y, true, textWidget);

				selection = textWidget.getSelectionRange();
				fireSelectionChanged(selection.x, selection.y);
			}
			break;

		case COPY:

			if (redraws()) {
				selection = getSelectedRange();
				if (selection.y == 0)
					copyMarkedRegion(false);
				else
					copyToClipboard(selection.x, selection.y, false, textWidget);
			}
			break;

		case DELETE:

			if (redraws()) {
				try {
					selection = getSelectedRange();
					Point widgetSelection = textWidget.getSelectionRange();
					if (selection.y == 0 || selection.y == widgetSelection.y)
						getTextWidget().invokeAction(ST.DELETE_NEXT);
					else
						deleteTextRange(selection.x, selection.y, textWidget);

					selection = textWidget.getSelectionRange();
					fireSelectionChanged(selection.x, selection.y);

				} catch (BadLocationException x) {
					// ignore
				}
			}
			break;

		case EXPAND_ALL:
			if (redraws())
				expandAll();
			break;

		case EXPAND:
			if (redraws()) {
				expand();
			}
			break;

		case COLLAPSE_ALL:
			if (redraws())
				collapseAll();
			break;

		case COLLAPSE:
			if (redraws()) {
				collapse();
			}
			break;

		default:
			super.doOperation(operation);
		}
	}

	/*
	 * @see org.eclipse.jface.text.source.SourceViewer#canDoOperation(int)
	 */
	public boolean canDoOperation(int operation) {

		switch (operation) {
		case COLLAPSE:
		case COLLAPSE_ALL:
		case EXPAND:
		case EXPAND_ALL:
			return isInlineProjectionMode();
		case TOGGLE:
			return isInlineProjectionMode() || !isSegmented();
		}

		return super.canDoOperation(operation);
	}

	private boolean isSegmented() {
		IDocument document = getDocument();
		int length = document == null ? 0 : document.getLength();
		IRegion visible = getModelCoverage();
		boolean isSegmented = visible != null
				&& !visible.equals(new Region(0, length));
		return isSegmented;
	}

	private IRegion getMarkedRegion() {
		if (getTextWidget() == null)
			return null;

		if (fMarkPosition == null || fMarkPosition.isDeleted())
			return null;

		int start = fMarkPosition.getOffset();
		int end = getSelectedRange().x;

		return start > end ? new Region(end, start - end) : new Region(start,
				end - start);
	}

	/*
	 * @see org.eclipse.jface.text.TextViewer#copyMarkedRegion(boolean)
	 */
	protected void copyMarkedRegion(boolean delete) {
		IRegion markedRegion = getMarkedRegion();
		if (markedRegion != null)
			copyToClipboard(markedRegion.getOffset(), markedRegion.getLength(),
					delete, getTextWidget());
	}

	private void copyToClipboard(int offset, int length, boolean delete,
			StyledText textWidget) {

		String copyText = null;

		try {
			IDocument document = getDocument();
			copyText = document.get(offset, length);
		} catch (BadLocationException ex) {
			// XXX: should log here, but JFace Text has no Log
			// As a fallback solution let the widget handle this
			textWidget.copy();
		}

		if (copyText != null && copyText.equals(textWidget.getSelectionText())) {
			/*
			 * XXX: Reduce pain of
			 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=64498 by letting
			 * the widget handle the copy operation in this special case.
			 */
			textWidget.copy();
		} else if (copyText != null) {

			Clipboard clipboard = new Clipboard(textWidget.getDisplay());

			try {
				Transfer[] dataTypes = new Transfer[] { TextTransfer
						.getInstance() };
				Object[] data = new Object[] { copyText };
				try {
					clipboard.setContents(data, dataTypes);
				} catch (SWTError e) {
					if (e.code != DND.ERROR_CANNOT_SET_CLIPBOARD)
						throw e;
					/*
					 * TODO see
					 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=59459 we
					 * should either log and/or inform the user silently fail
					 * for now.
					 */
					return;
				}

			} finally {
				clipboard.dispose();
			}
		}

		if (delete) {
			try {
				deleteTextRange(offset, length, textWidget);
			} catch (BadLocationException x) {
				// XXX: should log here, but JFace Text has no Log
			}
		}
	}

	private void deleteTextRange(int offset, int length, StyledText textWidget)
			throws BadLocationException {
		getDocument().replace(offset, length, null);
		int widgetCaret = modelOffset2WidgetOffset(offset);
		if (widgetCaret > -1)
			textWidget.setSelection(widgetCaret);
	}

	// @Override
	// public IRegion modelRange2WidgetRange(IRegion widgetRange) {
	// // TODO Auto-generated method stub
	// IRegion result = super.modelRange2WidgetRange(widgetRange);
	// System.out.println(widgetRange+" -> "+result);
	// return result;
	// }

	/**
	 * Adapts the behavior of the super class to respect line based folding.
	 * 
	 * @param widgetSelection
	 *            the widget selection
	 * @return the model selection while respecting line based folding
	 */
	protected Point widgetSelection2ModelSelection(Point widgetSelection) {

		if (!isInlineProjectionMode())
			return super.widgetSelection2ModelSelection(widgetSelection);

		/*
		 * There is one requirement that governs preservation of logical
		 * positions:
		 * 
		 * 1) a selection with widget_length == 0 should never expand to have
		 * model_length > 0.
		 * 
		 * There are a number of ambiguities to resolve with projection regions.
		 * A projected region P has a widget-length of zero. Its widget offset
		 * may interact with the selection S in various ways:
		 * 
		 * A) P.widget_offset lies at the caret, S.widget_length is zero.
		 * Requirement 1 applies. S is *behind* P (done so by
		 * widgetRange2ModelRange).
		 * 
		 * B) P.widget_offset lies inside the widget selection. This case is
		 * easy: P is included in S, which is automatically done so by
		 * widgetRange2ModelRange.
		 * 
		 * C) P.widget_offset lies at S.widget_end: This is arguable - our
		 * policy is to include P if it belongs to a projection annotation that
		 * overlaps with the widget selection.
		 * 
		 * D) P.widget_offset lies at S.widget_offset: Arguable - our policy is
		 * to include P if it belongs to a projection annotation that overlaps
		 * with the widget selection
		 */
		IRegion modelSelection = widgetRange2ModelRange(new Region(
				widgetSelection.x, widgetSelection.y));
		if (modelSelection == null)
			return null;

		int modelOffset = modelSelection.getOffset();
		int modelEndOffset = modelOffset + modelSelection.getLength();

		/* Case A: never expand a zero-length selection. S is *behind* P. */
		if (widgetSelection.y == 0)
			return new Point(modelEndOffset, 0);

		int widgetSelectionExclusiveEnd = widgetSelection.x + widgetSelection.y;
		Position[] annotationPositions = computeOverlappingAnnotationPositions(modelSelection);
		for (int i = 0; i < annotationPositions.length; i++) {
			IRegion[] regions = computeCollapsedRegions(annotationPositions[i]);
			if (regions == null)
				continue;
			for (int j = 0; j < regions.length; j++) {
				IRegion modelRange = regions[j];
				IRegion widgetRange = modelRange2ClosestWidgetRange(modelRange);
				// only take collapsed ranges, i.e. widget length is 0
				if (widgetRange != null && widgetRange.getLength() == 0) {
					int widgetOffset = widgetRange.getOffset();
					// D) region is collapsed at S.widget_offset
					if (widgetOffset == widgetSelection.x)
						modelOffset = Math.min(modelOffset, modelRange
								.getOffset());
					// C) region is collapsed at S.widget_end
					else if (widgetOffset == widgetSelectionExclusiveEnd)
						modelEndOffset = Math.max(modelEndOffset, modelRange
								.getOffset()
								+ modelRange.getLength());
				}
			}
		}
		return new Point(modelOffset, modelEndOffset - modelOffset);
	}

	/**
	 * Returns the positions of all annotations that intersect with
	 * <code>modelSelection</code> and that are at least partly visible.
	 * 
	 * @param modelSelection
	 *            a model range
	 * @return the positions of all annotations that intersect with
	 *         <code>modelSelection</code>
	 * @since 3.1
	 */
	private Position[] computeOverlappingAnnotationPositions(
			IRegion modelSelection) {
		List positions = new ArrayList();
		for (Iterator e = fInlineProjectionAnnotationModel
				.getAnnotationIterator(); e.hasNext();) {
			InlineProjectionAnnotation annotation = (InlineProjectionAnnotation) e
					.next();
			Position position = fInlineProjectionAnnotationModel
					.getPosition(annotation);
			if (position != null
					&& position.overlapsWith(modelSelection.getOffset(),
							modelSelection.getLength())
					&& modelRange2WidgetRange(position) != null)
				positions.add(position);
		}
		return (Position[]) positions.toArray(new Position[positions.size()]);
	}

	/*
	 * @see org.eclipse.jface.text.TextViewer#getFindReplaceDocumentAdapter()
	 */
	protected FindReplaceDocumentAdapter getFindReplaceDocumentAdapter() {
		if (fFindReplaceDocumentAdapter == null) {
			IDocument document = isInlineProjectionMode() ? getDocument()
					: getVisibleDocument();
			fFindReplaceDocumentAdapter = new FindReplaceDocumentAdapter(
					document);
		}
		return fFindReplaceDocumentAdapter;
	}

	/*
	 * @see org.eclipse.jface.text.TextViewer#findAndSelect(int,
	 * java.lang.String, boolean, boolean, boolean, boolean)
	 */
	protected int findAndSelect(int startPosition, String findString,
			boolean forwardSearch, boolean caseSensitive, boolean wholeWord,
			boolean regExSearch) {

		if (!isInlineProjectionMode())
			return super.findAndSelect(startPosition, findString,
					forwardSearch, caseSensitive, wholeWord, regExSearch);

		StyledText textWidget = getTextWidget();
		if (textWidget == null)
			return -1;

		try {

			IRegion matchRegion = getFindReplaceDocumentAdapter().find(
					startPosition, findString, forwardSearch, caseSensitive,
					wholeWord, regExSearch);
			if (matchRegion != null) {
				exposeModelRange(matchRegion);
				revealRange(matchRegion.getOffset(), matchRegion.getLength());
				setSelectedRange(matchRegion.getOffset(), matchRegion
						.getLength());
				return matchRegion.getOffset();
			}

		} catch (BadLocationException x) {
		}

		return -1;
	}

	/*
	 * @see org.eclipse.jface.text.TextViewer#findAndSelectInRange(int,
	 * java.lang.String, boolean, boolean, boolean, int, int, boolean)
	 */
	protected int findAndSelectInRange(int startPosition, String findString,
			boolean forwardSearch, boolean caseSensitive, boolean wholeWord,
			int rangeOffset, int rangeLength, boolean regExSearch) {

		if (!isInlineProjectionMode())
			return super.findAndSelectInRange(startPosition, findString,
					forwardSearch, caseSensitive, wholeWord, rangeOffset,
					rangeLength, regExSearch);

		StyledText textWidget = getTextWidget();
		if (textWidget == null)
			return -1;

		try {

			int modelOffset = startPosition;
			if (forwardSearch
					&& (startPosition == -1 || startPosition < rangeOffset)) {
				modelOffset = rangeOffset;
			} else if (!forwardSearch
					&& (startPosition == -1 || startPosition > rangeOffset
							+ rangeLength)) {
				modelOffset = rangeOffset + rangeLength;
			}

			IRegion matchRegion = getFindReplaceDocumentAdapter().find(
					modelOffset, findString, forwardSearch, caseSensitive,
					wholeWord, regExSearch);
			if (matchRegion != null) {
				int offset = matchRegion.getOffset();
				int length = matchRegion.getLength();
				if (rangeOffset <= offset
						&& offset + length <= rangeOffset + rangeLength) {
					exposeModelRange(matchRegion);
					revealRange(offset, length);
					setSelectedRange(offset, length);
					return offset;
				}
			}

		} catch (BadLocationException x) {
		}

		return -1;
	}

	// /**
	// * disable classic projection
	// */
	// public ProjectionAnnotationModel getProjectionAnnotationModel() {
	// return null;
	// }

	private List fInlineProjectionListeners;

	/**
	 * Notifies all registered projection listeners that projection mode has
	 * been enabled.
	 */
	protected void fireInlineProjectionEnabled() {
		if (fInlineProjectionListeners != null) {
			Iterator e = new ArrayList(fInlineProjectionListeners).iterator();
			while (e.hasNext()) {
				IInlineProjectionListener l = (IInlineProjectionListener) e
						.next();
				l.inlineProjectionEnabled();
			}
		}
	}

	/**
	 * Notifies all registered projection listeners that projection mode has
	 * been disabled.
	 */
	protected void fireInlineProjectionDisabled() {
		if (fInlineProjectionListeners != null) {
			Iterator e = new ArrayList(fInlineProjectionListeners).iterator();
			while (e.hasNext()) {
				IInlineProjectionListener l = (IInlineProjectionListener) e
						.next();
				l.inlineProjectionDisabled();
			}
		}
	}

	/**
	 * Adds a projection annotation listener to this viewer. The listener may
	 * not be <code>null</code>. If the listener is already registered, this
	 * method does not have any effect.
	 * 
	 * @param listener
	 *            the listener to add
	 */
	public void addProjectionListener(IProjectionListener listener) {
		super.addProjectionListener(listener);

		if (listener instanceof IInlineProjectionListener) {

			if (fInlineProjectionListeners == null)
				fInlineProjectionListeners = new ArrayList();

			if (!fInlineProjectionListeners.contains(listener))
				fInlineProjectionListeners.add(listener);
		}
	}

	/**
	 * Removes the given listener from this viewer. The listener may not be
	 * <code>null</code>. If the listener is not registered with this viewer,
	 * this method is without effect.
	 * 
	 * @param listener
	 *            the listener to remove
	 */
	public void removeProjectionListener(IProjectionListener listener) {
		super.removeProjectionListener(listener);

		if (listener instanceof IInlineProjectionListener) {

			if (fInlineProjectionListeners != null) {
				fInlineProjectionListeners.remove(listener);
				if (fInlineProjectionListeners.size() == 0)
					fInlineProjectionListeners = null;
			}
		}
	}
}