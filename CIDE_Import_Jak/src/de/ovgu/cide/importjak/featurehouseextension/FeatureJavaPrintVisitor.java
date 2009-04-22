package de.ovgu.cide.importjak.featurehouseextension;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

import org.prop4j.Implies;
import org.prop4j.Node;

import printer.ArtifactPrintVisitor;
import printer.PrintVisitorException;
import tmp.generated_jak.SimplePrintVisitor;
import de.ovgu.cide.fstgen.ast.FSTNode;
import de.ovgu.cide.fstgen.ast.FSTNonTerminal;

public class FeatureJavaPrintVisitor extends ArtifactPrintVisitor {
	public static String startIfdef(Node featureExpression) {
		return "/*IF[" + featureExpression.toString() + "]*/";
	}

	public static String endIfdef(Node featureExpression) {
		return "/*ENDIF[" + featureExpression.toString() + "]*/";
	}

	public static class FeatureJavaSimplePrintVisitor extends
			SimplePrintVisitor {

		public FeatureJavaSimplePrintVisitor(PrintStream printStream) {
			super(printStream);
		}

		protected void printFeatures(FSTNode node, boolean start) {

			if (node.getType().equals("CompilationUnit")
					|| !coveredByParent(node, node.getParent())) {
				if (start)
					printToken(startIfdef(FeatureExpressions.get(node)));
				else
					printToken(endIfdef(FeatureExpressions.get(node)));
			}

		}

		private boolean coveredByParent(FSTNode node, FSTNode parent) {
			if (GuidslFileLoader.featureModel.checkCondition(new Implies(
					FeatureExpressions.get(parent), FeatureExpressions
							.get(node))))
				return true;
			if (parent.getParent() != null)
				return coveredByParent(node, parent.getParent());
			return false;
		}
	}

	public FeatureJavaPrintVisitor() {
		super("Java-File");
	}

	public void processNode(FSTNode node, File folderPath)
			throws PrintVisitorException {
		if (node instanceof FSTNonTerminal) {
			FSTNonTerminal nonterminal = (FSTNonTerminal) node;
			for (FSTNode child : nonterminal.getChildren()) {
				String fileName = folderPath.getPath() + File.separator
						+ nonterminal.getName();

				SimplePrintVisitor visitor;
				try {
					visitor = new FeatureJavaSimplePrintVisitor(
							new PrintStream(fileName));
					visitor.visit((FSTNonTerminal) child);
					visitor.getResult();
				} catch (FileNotFoundException e) {
					throw new PrintVisitorException(e.getMessage());
				}
			}
		} else {
			assert (!(node instanceof FSTNonTerminal));
		}
	}
}
