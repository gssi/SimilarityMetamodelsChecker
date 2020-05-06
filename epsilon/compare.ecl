pre variables {
var simmetrics : new Native('org.epsilon.ecl.tools.textcomparison.simmetrics.SimMetricsTool');
var nrclasses: Real=Source!EClass.all.size();
var nrattributes: Real=Source!EAttribute.all.size();
var nrreferences: Real=Source!EReference.all.size();
}

rule EClass
  match s : Source!EClass
  with v : Target!EClass {
  
  compare {
  
    if(s.name.fuzzyMatch(v.name)){
    	
    	return true;
    }else{
    	return false;
    }
  }
  
}

rule EAttribute
  match s : Source!EAttribute
  with v : Target!EAttribute {
  
  compare {
  	if(s.name.fuzzyMatch(v.name) and s.etype.isDefined() and v.etype.isDefined() and s.etype.name.fuzzyMatch(v.etype.name) and s.eContainingClass.name.fuzzyMatch(v.eContainingClass.name)){
    	//("Feature "+s.name+" matching").println();
    	
    	return true;
    }else{
     	
    	return false;
    }
  }
}

rule EReference
  match s : Source!EReference
  with v : Target!EReference {
  
  compare {
  	if(s.name.fuzzyMatch(v.name) and s.etype.name.fuzzyMatch(v.etype.name) and s.eContainingClass.name.fuzzyMatch(v.eContainingClass.name) and s.lowerBound==v.lowerBound and s.upperBound==v.upperBound and s.unique==v.unique and s.containment==v.containment){
    	//("Feature "+s.name+" matching").println();
    	
    	return true;
    }else{
    	return false;
    }
  }
}

rule EPackage
  match s : Source!EPackage
  with v : Target!EPackage {
  
  compare {
  	//("EPackage "+s.name).println();
  	
    return true;
  }
}

operation String fuzzyMatch(other: String): Boolean {
	return simmetrics.similarity(self,other,'Levenshtein') >0.5;
}


