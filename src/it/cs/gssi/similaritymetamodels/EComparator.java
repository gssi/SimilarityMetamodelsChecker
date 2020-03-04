/*******************************************************************************
 * Copyright (c) 2008 The University of York.
 * This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * Contributors:
 *     Dimitrios Kolovos - initial API and implementation
 ******************************************************************************/
package it.cs.gssi.similaritymetamodels;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.epsilon.ecl.EclModule;
import org.eclipse.epsilon.ecl.IEclModule;
import org.eclipse.epsilon.ecl.trace.MatchTrace;
import org.eclipse.epsilon.eol.EolModule;
import org.eclipse.epsilon.eol.IEolModule;
import org.eclipse.epsilon.eol.exceptions.models.EolModelElementTypeNotFoundException;
import org.eclipse.epsilon.eol.models.IModel;
import it.cs.gssi.similaritymetamodels.EpsilonStandaloneExample;


/**
 * This example demonstrates using the 
 * Epsilon Object Language, the core language
 * of Epsilon, in a stand-alone manner 
 * @author Dimitrios Kolovos
 */
public class EComparator extends EpsilonStandaloneExample {
	
	
	public EComparator(String source, String target) {
		super();
		this.source = source;
		this.target = target;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public void setSource(String source) {
		this.source = source;
	}

	private String source;
	private String target;
	
	public static void main(String[] args) throws Exception {
		
	}
	
	@Override
	public IEclModule createModule() {
		return new EclModule();
	}

	@Override
	public List<IModel> getModels(String source, String target) throws Exception {
		EPackage.Registry.INSTANCE.put(EcorePackage.eNS_URI, EcorePackage.eINSTANCE);
		List<IModel> models = new ArrayList<IModel>();
		models.add(createEmfModel("Source", source, 
				"support/Ecore.ecore", true, false));
		models.add(createEmfModel("Target", target, 
				"support/Ecore.ecore", true, false));
		
		return models;
	}

	@Override
	public String getSource() throws Exception {
		return "epsilon/compare.ecl";
	}

	@Override
	public int postProcess() {
		MatchTrace  trace=(MatchTrace) result;
		int sim=0;
		MatchTrace successfull=(trace.getReduced());
		IModel source;
		try {
			source = getModels(this.source, this.target).get(0);
			sim=checkComparison(source, successfull);
			//System.out.println("Similarity resulted: "+sim);
			
		//System.out.println("Matching everything");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return sim;
	}
	
	public int checkComparison(IModel modeltocheck, MatchTrace successfull) {
		
		int simIndex=0;
		Double classsim=0.0;
		Double featuresim=0.0;
		int nrclasses=0;
		int nrfeats=0;
		try {
			nrfeats=modeltocheck.getAllOfKind("EStructuralFeature").size();
			
			Collection<EClass> classes;
			classes = (Collection<EClass>) modeltocheck.getAllOfKind("EClass");
			
			nrclasses=classes.size();
			
			for (EClass eClass : classes) {
				if(successfull.getMatch(eClass)==null) {
					
					//System.err.println("not matching :"+eClass.getName());
					
				}else {
					classsim++;
					for (EStructuralFeature f : eClass.getEStructuralFeatures()) {
						if(successfull.getMatch(f)==null) {
							
							//System.err.println("not matching :"+f.getName()+ " in class "+f.getEContainingClass().getName());
							
						}else {
							featuresim++;
						}
						
					}
				
				}
				
			}
			
		} catch (EolModelElementTypeNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//System.err.println("("+classsim+"*100/"+nrclasses+")"+"("+featuresim+"*100/"+nrfeats+")");
		simIndex= (int) ((((classsim*100)/nrclasses)+((featuresim*100)/nrfeats))/2);
		return simIndex;
		
		
	}
	
}
