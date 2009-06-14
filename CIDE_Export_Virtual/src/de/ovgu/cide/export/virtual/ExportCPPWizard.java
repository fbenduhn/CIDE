package de.ovgu.cide.export.virtual;

import org.eclipse.core.resources.IProject;

import de.ovgu.cide.export.BaseExportJob;
import de.ovgu.cide.export.CIDEExportWizard;
import de.ovgu.cide.export.virtual.internal.CPPExportOptions;
import de.ovgu.cide.export.virtual.internal.Export2PPJob;
import de.ovgu.cide.features.FeatureModelNotFoundException;

public class ExportCPPWizard extends CIDEExportWizard {

	@Override
	protected BaseExportJob createExportJob(IProject sourceProject,
			IProject targetProject) {
		try {
			return new Export2PPJob("Exporting CIDE -> CPP", sourceProject,
					targetProject, new CPPExportOptions());
		} catch (FeatureModelNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

}
