/**
    Copyright 2010 Christian K�stner

    This file is part of CIDE.

    CIDE is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, version 3 of the License.

    CIDE is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with CIDE.  If not, see <http://www.gnu.org/licenses/>.

    See http://www.fosd.de/cide/ for further information.
*/

package cide.astgen.nparser.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cide.astgen.nparser.visitor.NVisitor;

public class NProduction {

	final List<NChoice> choices = new ArrayList<NChoice>();
	private final List<NAnnotation> annotations = new ArrayList<NAnnotation>();

	private NGrammar grammar;

	public String name;

	public NProduction(NGrammar grammar, String name) {
		this.setGrammar(grammar);
		this.name = name;
	}

	public NProduction(NGrammar grammar) {
		this.setGrammar(grammar);
	}

	public void setName(String name) {
		this.name = name;
	}

	public void accept(NVisitor visitor) {
		if (visitor.visit(this))
			for (NChoice p : choices)
				p.accept(visitor);
		visitor.postVisit(this);
	}

	public String getName() {
		return name.toString();
	}

	public boolean isFirstProduction() {
		return getGrammar().productions.size() > 0
				&& getGrammar().productions.get(0) == this;
	}

	public List<NChoice> getChoices() {
		return Collections.unmodifiableList(choices);
	}

	public void addChoice(NChoice c) {
		choices.add(c);
	}

	/**
	 * only used internally. query annotations from a choice instead.
	 * @return
	 */
	List<NAnnotation> getAnnotations() {
		return Collections.unmodifiableList(annotations);
	}

	/** 
	 * conceptually adds an annotation to all choices
	 * @param annotation
	 */
	public void addAnnotation(NAnnotation annotation) {
		annotations.add(annotation);
	}


	public void setGrammar(NGrammar grammar) {
		this.grammar = grammar;
	}

	public NGrammar getGrammar() {
		return grammar;
	}
	
	@Override
	public String toString() {
		String r = name + " : ";
		for (NChoice k : choices)
			r += "\n\t"+k.toString()+" |";
		r += ";";
		return r;
	}
}
