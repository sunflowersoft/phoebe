package vy.phoebe.regression;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;

import vy.phoebe.math.MathUtil;
import vy.phoebe.util.Pair;

public class RModelGroupList implements List<RModelList> {

	private ArrayList<RModelList> modelListList = new ArrayList<RModelList>();

	
	public RModelGroupList() {
    	
    }

	
	public RModelGroupList(Collection<? extends RModelList> c) {
    	addAll(c);
    }

	
	public void makeCommonChosen(int maxNumber) {
		// Yellow color
		if (this.size() < 2 || maxNumber < 0)
			return;
		
		RModelList init = this.get(0);
		HashSet<String> commonRegressors = new HashSet<String>(init.getAllRegressors());
		
		for (int i = 1; i < this.size(); i++) {
			RModelList modelList = this.get(i);
			HashSet<String> regressors = new HashSet<String>(modelList.getAllRegressors());
			commonRegressors.retainAll(regressors);
		}
		
		if (commonRegressors.size() == 0)
			return;
		
		
		for (RModelList modelList : this) {
			modelList.clearChosenList();
			
			ArrayList<Pair<RModel>> pairList = 
				new ArrayList<Pair<RModel>>();
			
			for (RModel model : modelList) {
				RModelAssoc assoc = new RModelAssoc(model);
				int count = assoc.countRegressorsExist(commonRegressors);
				if (count < 1) 
					continue;
				
				Pair<RModel> pair = new Pair<RModel>(model, count);
				pairList.add(pair);
			}
			if (maxNumber == 0)
				Pair.sort(pairList, true);
			else
				Pair.sort(pairList, maxNumber, true);
			
			ArrayList<RModel> chosen = Pair.getKeyList(pairList);
			for (RModel model : chosen)
				modelList.choose(model);
		}
		
	}

	
	public void regroup(int maxResults, boolean fixed) {

		RModelGroupList inputModelGroupList = new RModelGroupList(this);
		
		this.clear();
		
		if (inputModelGroupList.size() == 0)
			return;
		else if (inputModelGroupList.size() == 1) {
			RModelList modelList = inputModelGroupList.get(0);
			for (RModel model : modelList) {
				RModelList list = new RModelListEx();
				list.add(model);
				this.add(list);
			}
			return;
		}
		
		boolean mono = true;
		for (RModelList modelList : inputModelGroupList) {
			if (modelList.size() > 1) {
				mono = false;
				break;
			}
		}
		// Case: each list has only one model
		if (mono) {
			RModelList newModelList = new RModelListEx();
			for (RModelList modelList : inputModelGroupList) {
				newModelList.add(modelList.get(0));
			}
			this.add(newModelList);
			return;
		}
		
		
		List<Set<RModel>> list = new ArrayList<Set<RModel>>();
		for (RModelList modelList : inputModelGroupList) {
			Set<RModel> set = new HashSet<RModel>(modelList);
			
			list.add(set);
		}
		
		List<Set<RModel>> cartesian = MathUtil.cartesianProduct(list);
		if (cartesian.size() == 0)
			return;
		
		if (cartesian.size() == 1) {
			RModelList modelList = new RModelListEx(
					cartesian.get(0));
			this.add(modelList);
			this.orderingEachListBy(inputModelGroupList);
			return;
		}
		
		ArrayList<Pair<Set<RModel>>> pairList = 
			new ArrayList<Pair<Set<RModel>>>();
		
		for (Set<RModel> set : cartesian) {
			Set<String> common = new HashSet<String>();
			Set<String> union = new HashSet<String>();
			
			int i = 0;
			for (RModel model : set) {
				ArrayList<String> names = model.getRegressorNames();
				
				if (i == 0) {
					common.addAll(names);
					union.addAll(names);
				}
				else {
					common.retainAll(names);
					union.addAll(names);
				}
				i++;
			}
			
			Set<String> complement = new HashSet<String>(union);
			complement.removeAll(common);
			
			if (fixed) {
				if (complement.size() != 0)
					continue;
			}
			int coeff = common.size() - complement.size();
			if (coeff <= 0)
				continue;
			
			Pair<Set<RModel>> pair = 
				new Pair<Set<RModel>>(set, coeff);
			pairList.add(pair);
		}
		
		if (maxResults == 0)
			Pair.sort(pairList, true);
		else
			Pair.sort(pairList, maxResults, true);
		
		ArrayList<Set<RModel>> setList = Pair.getKeyList(pairList);
		for (Set<RModel> set : setList) {
			RModelList modelList = new RModelListEx(set);
			this.add(modelList);
		}

		// Ordering each list
		this.orderingEachListBy(inputModelGroupList);
	}

	
	private void orderingEachListBy(RModelGroupList other) {
		ArrayList<String> rNameList = other.getResponseList();
		
		for (RModelList modelList : this) {
			ArrayList<RModel> newlist = 
				new ArrayList<RModel>();
			HashMap<Integer, RModel> map = 
				new HashMap<Integer, RModel>();
			
			for (RModel model : modelList) {
				RModel found = null;
				for (String rName : rNameList) {
					int idx = other.indexOf(rName);
					if (idx == -1) continue;
					RModelList mList = other.get(idx);
					
					for (RModel m : mList) {
						if (m == model) {
							found = model;
							map.put(rNameList.indexOf(rName), model);
							break;
						}
					}
					
					if (found != null)
						break;
				}
			}
			
			ArrayList<Integer> keys = new ArrayList<Integer>(map.keySet());
			Collections.sort(keys);
			
			for (Integer key : keys) {
				newlist.add(map.get(key));
			}
			
			modelList.clear();
			modelList.addAll(newlist);
		}
		
	}
	
	
	public ArrayList<String> getResponseList() {
		ArrayList<String> rList = new ArrayList<String>();
		for (RModelList modelList : this) {
			rList.add(modelList.getResponse());
		}
		return rList;
	}
	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < modelListList.size(); i++) {
			RModelList modelList = modelListList.get(i);
			if (i > 0)
				buffer.append("\n\n\n");
			
			buffer.append("Group " + (i + 1) + " = {" + "\n");
			for (int j = 0; j < modelList.size(); j++) {
				RModel model = modelList.get(j);
				if (j > 0)
					buffer.append("\n\n");
				
				buffer.append("    " + model.toString());
			}
			buffer.append("\n}");
		}
		
		return buffer.toString();
	}

	
	public void toExcel(File file) {
		
		try {
			WritableWorkbook workbook = Workbook.createWorkbook(file);
			WritableSheet sheet = workbook.createSheet("Phoebe", 0);
			
			WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
					WritableFont.BOLD, false);
			WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);
	
			WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
					WritableFont.NO_BOLD, false);
			WritableCellFormat cellFormat12 = new WritableCellFormat(font12);
			
			int rowStep = 0;
			Label lblSpec = new Label(1, rowStep, "Specification", boldCellFormat12);
			sheet.addCell(lblSpec);
			Label lblType = new Label(2, rowStep, "Type", boldCellFormat12);
			
			sheet.addCell(lblType);
			Label lblFitness = new Label(3, rowStep, "Fitness", boldCellFormat12);
			sheet.addCell(lblFitness);
			Label lblR = new Label(4, rowStep, "R", boldCellFormat12);
			sheet.addCell(lblR);
			Label lblSS = new Label(5, rowStep, "SS", boldCellFormat12);
			sheet.addCell(lblSS);
			
			Label lblMean = new Label(6, rowStep, "Mean", boldCellFormat12);
			sheet.addCell(lblMean);
			Label lblSd = new Label(7, rowStep, "Sd", boldCellFormat12);
			sheet.addCell(lblSd);

			Label lblErrMean = new Label(8, rowStep, "Error mean", boldCellFormat12);
			sheet.addCell(lblErrMean);
			Label lblErrSd = new Label(9, rowStep, "Error sd", boldCellFormat12);
			sheet.addCell(lblErrSd);
			
			Label lblPecentErrMean = new Label(10, rowStep, "Error mean (%)", boldCellFormat12);
			sheet.addCell(lblPecentErrMean);
			Label lblPercentErrSd = new Label(11, rowStep, "Error sd (%)", boldCellFormat12);
			sheet.addCell(lblPercentErrSd);
			
			Label lblPvalue = new Label(12, rowStep, "P-value", boldCellFormat12);
			sheet.addCell(lblPvalue);

			rowStep ++;
			for (int i = 0; i < size(); i++) {
				RModelList modelList = get(i);
				
				Label no = new Label(0, rowStep, "Group " + (i+1), boldCellFormat12);
	    		sheet.addCell(no);
	    		
	    		for (int j = 0; j < modelList.size(); j++) {
	    			RModel model = modelList.get(j);
	    			Label spec = new Label(
	    					1, 
	    					rowStep, 
	    					model.getSpec(), 
	    					cellFormat12);
		    		sheet.addCell(spec);
		    		
	    			Label type = new Label(
	    					2, 
	    					rowStep, 
	    					model.getType(), 
	    					cellFormat12);
		    		sheet.addCell(type);

		    		Number fitness = new Number(
	    					3, 
	    					rowStep, 
	    					MathUtil.round(model.getFitness()), 
	    					cellFormat12);
	    			sheet.addCell(fitness);
	    			
	    			Number r = new Number(
	    					4, 
	    					rowStep, 
	    					MathUtil.round(model.getR()), 
	    					cellFormat12);
	    			sheet.addCell(r);

	    			Number ss = new Number(
	    					5, 
	    					rowStep, 
	    					MathUtil.round(model.getSumOfSquares()), 
	    					cellFormat12);
	    			sheet.addCell(ss);
	
	    			Number mean = new Number(
	    					6, 
	    					rowStep, 
	    					MathUtil.round(model.getMean()), 
	    					cellFormat12);
	    			sheet.addCell(mean);

	    			Number sd = new Number(
	    					7, 
	    					rowStep, 
	    					MathUtil.round(model.getSd()), 
	    					cellFormat12);
	    			sheet.addCell(sd);

	    			Number errMean = new Number(
	    					8, 
	    					rowStep, 
	    					MathUtil.round(model.getErrMean()), 
	    					cellFormat12);
	    			sheet.addCell(errMean);

	    			Number errSd = new Number(
	    					9, 
	    					rowStep, 
	    					MathUtil.round(model.getErrSd()), 
	    					cellFormat12);
	    			sheet.addCell(errSd);

	    			Label ratioErrMean = new Label(
	    					10, 
	    					rowStep, 
	    					MathUtil.round(model.getRatioErrMean() * 100.0) + "%", 
	    					cellFormat12);
	    			sheet.addCell(ratioErrMean);

	    			Label ratioErrSd = new Label(
	    					11, 
	    					rowStep, 
	    					MathUtil.round(model.getRatioErrSd() * 100.0) + "%", 
	    					cellFormat12);
	    			sheet.addCell(ratioErrSd);

	    			Label pvalue = new Label(
	    					12, 
	    					rowStep, 
	    					MathUtil.format(model.getPvalue()), 
	    					cellFormat12);
	    			sheet.addCell(pvalue);

	    			rowStep ++;
	    		}
	    		
				rowStep ++;
			}
			
			workbook.write();
			workbook.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
}
	
	
	@Override
	public boolean add(RModelList e) {
		// TODO Auto-generated method stub
		return modelListList.add(e);
	}

	@Override
	public void add(int index, RModelList element) {
		// TODO Auto-generated method stub
		modelListList.add(index, element);
	}

	@Override
	public boolean addAll(Collection<? extends RModelList> c) {
		// TODO Auto-generated method stub
		return modelListList.addAll(c);
	}

	@Override
	public boolean addAll(int index, Collection<? extends RModelList> c) {
		// TODO Auto-generated method stub
		return modelListList.addAll(index, c);
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		modelListList.clear();
	}

	@Override
	public boolean contains(Object o) {
		// TODO Auto-generated method stub
		return modelListList.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return modelListList.containsAll(c);
	}

	@Override
	public RModelList get(int index) {
		// TODO Auto-generated method stub
		return modelListList.get(index);
	}

	@Override
	public int indexOf(Object o) {
		// TODO Auto-generated method stub
		return modelListList.indexOf(o);
	}

	public int indexOf(String response) {
		for (int i = 0; i < modelListList.size(); i++) {
			RModelList modelList = modelListList.get(i);
			String res = modelList.getResponse();
			if (res != null && res.equals(response))
				return i;
		}
		
		return -1;
	}
	
	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return modelListList.isEmpty();
	}

	@Override
	public Iterator<RModelList> iterator() {
		// TODO Auto-generated method stub
		return modelListList.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		// TODO Auto-generated method stub
		return modelListList.lastIndexOf(o);
	}

	@Override
	public ListIterator<RModelList> listIterator() {
		// TODO Auto-generated method stub
		return modelListList.listIterator();
	}

	@Override
	public ListIterator<RModelList> listIterator(int index) {
		// TODO Auto-generated method stub
		return modelListList.listIterator(index);
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return modelListList.remove(o);
	}

	@Override
	public RModelList remove(int index) {
		// TODO Auto-generated method stub
		return modelListList.remove(index);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return modelListList.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		// TODO Auto-generated method stub
		return modelListList.retainAll(c);
	}

	@Override
	public RModelList set(int index, RModelList element) {
		// TODO Auto-generated method stub
		return modelListList.set(index, element);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return modelListList.size();
	}

	@Override
	public List<RModelList> subList(int fromIndex, int toIndex) {
		// TODO Auto-generated method stub
		return modelListList.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return modelListList.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		// TODO Auto-generated method stub
		return modelListList.toArray(a);
	}


	
	
}
