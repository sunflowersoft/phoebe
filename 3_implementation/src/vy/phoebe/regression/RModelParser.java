package vy.phoebe.regression;

import vy.phoebe.dataset.Dataset;


public interface RModelParser {

	
	void putProp(String key, Object value);

	
	Object getProp(String key);

	
	void setDataset(Dataset dataset);
	
	
	RModel parse(String modelSpec);
	
	
	RModel parseExpr(String desc);

	
	RModelList parseExprs(String text);
}
