package vy.phoebe.regression;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import vy.phoebe.Config;
import vy.phoebe.math.MathUtil;
import vy.phoebe.util.Pair;

public abstract class RModelList implements List<RModel> {

	protected ArrayList<RModel> modelList = new ArrayList<RModel>();
	
	
	protected String response = "";
	
	
	public RModelList() {
    	
    }

	
	public RModelList(Collection<? extends RModel> c) {
    	addAll(c);
    }

	
	public RModelList(RModelList modelList) {
    	this.addAll(modelList);
    }
	
	
	public abstract boolean isChosen(RModel model);

	
	public abstract void removeChosen(RModel model);

	
	public abstract void choose(RModel model);
		
	
	public abstract void clearChosenList();
	
	
	public abstract ArrayList<RModel> getChosenList();
	
	
	public abstract void setChosenList(ArrayList<RModel> chosenList);

	
	public abstract void addChosenList(ArrayList<RModel> chosenList);

		
	public void setResponse(String response) {
		this.response = response;
	}
	
	
	public String getResponse() {
		return this.response;
	}
	
	
	private double getAvg(String name) {
		
		if (size() == 0)
			return 0;
		
		double average = 0;
		for (RModel model : this) {
			double value = 0;
			if (name.equals("fitness"))
				value = model.getFitness();
			else if (name.equals("r"))
				value = model.getR();
			else if (name.equals("mean"))
				value = model.getMean();
			else if (name.equals("sd"))
				value = model.getSd();
			else if (name.equals("errmean"))
				value = model.getErrMean();
			else if (name.equals("errsd"))
				value = model.getErrSd();
			else if (name.equals("ratioerrmean"))
				value = model.getRatioErrMean();
			else if (name.equals("ratioerrsd"))
				value = model.getRatioErrSd();
			else if (name.equals("ss"))
				value = model.getSumOfSquares();
				
			average += value;
		}
		
		return average / size();
		
	}
	
	
	public double getAvgFitness() {
		return getAvg("fitness");
	}
	
	
	public double getAvgR() {
		return getAvg("r");
	}

	
	public double getAvgMean() {
		return getAvg("mean");
	}

	
	public double getAvgSd() {
		return getAvg("sd");
	}

	
	public double getAvgErrMean() {
		return getAvg("errmean");
	}

	
	public double getAvgErrSd() {
		return getAvg("errsd");
	}

	
	public double getAvgRatioErrMean() {
		return getAvg("ratioerrmean");
	}

	
	public double getAvgRatioErrSd() {
		return getAvg("ratioerrsd");
	}

	
	public double getAvgSumOfSquares() {
		return getAvg("ss");
	}

	
	public ArrayList<String> getCommonRegressors() {
		
		Set<String> common = new HashSet<String>();
		for (int i = 0; i < size(); i++) {
			RModel model = get(i);
			
			ArrayList<String> names = model.getRegressorNames();
			
			if (i == 0)
				common.addAll(names);
			else
				common.retainAll(names);
		}
		
		return new ArrayList<String>(common);
	}
	
	
	public ArrayList<String> getAllRegressors() {
		HashSet<String> regressors = new HashSet<String>();
		for (RModel model : modelList) {
			regressors.addAll(model.getRegressorNames());
		}
		
		return new ArrayList<String>(regressors);
	}
	
	
	public String getAllSpecs() {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			RModel model = get(i);
			if ( i > 0)
				buffer.append("\n");
			
			String spec = RModelAssoc.prettySpec(model.getSpec());
			buffer.append(spec);
		}
		
		return buffer.toString();
	}
	
	
	public RModelList compare(RModelList other) {
		RModelList newModelList = new RModelListEx(this);
		newModelList.addAll(other);
		
		ArrayList<Pair<RModel>> pairList = 
			new ArrayList<Pair<RModel>>();
		for (RModel model : newModelList) {
			Pair<RModel> pair = 
				new Pair<RModel>(model, model.getFitness());
			pairList.add(pair);
		}
		Pair.sort(pairList, true);
		
		newModelList.clear();
		newModelList.addAll(Pair.getKeyList(pairList));
		newModelList.setChosenList(this.getChosenList());
		
		return newModelList;
	}
	
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < modelList.size(); i++) {
			RModel model = modelList.get(i);
			
			if (i > 0)
				buffer.append("\n\n");
			
			if (isChosen(model))
				buffer.append(
						RModel.BEGIN_CHOSEN_SIGN + 
						model.toString() + 
						RModel.END_CHOSEN_SIGN);
			else
				buffer.append(model.toString());
		}
		
		return buffer.toString();
	}
	
	
	public void toExcel(File file) throws Exception {
		WritableWorkbook workbook = Workbook.createWorkbook(file);
		
		WritableSheet sheet = workbook.createSheet("Phoebe", 0);
		
		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);
		
		Label lblSpec = new Label(0, 0, "Specification", boldCellFormat12);
		sheet.addCell(lblSpec);
		Label lblType = new Label(1, 0, "Type", boldCellFormat12);
		sheet.addCell(lblType);
		
		Label lblFitness = new Label(2, 0, "Fitness", boldCellFormat12);
		sheet.addCell(lblFitness);
		Label lblR = new Label(3, 0, "R", boldCellFormat12);
		sheet.addCell(lblR);
		Label lblSS = new Label(4, 0, "SS", boldCellFormat12);
		sheet.addCell(lblSS);
		
		
		Label lblMean = new Label(5, 0, "Mean", boldCellFormat12);
		sheet.addCell(lblMean);
		Label lblSd = new Label(6, 0, "Sd", boldCellFormat12);
		sheet.addCell(lblSd);

		
		Label lblErrMean = new Label(7, 0, "Error mean", boldCellFormat12);
		sheet.addCell(lblErrMean);
		Label lblErrSd = new Label(8, 0, "Error sd", boldCellFormat12);
		sheet.addCell(lblErrSd);
		
		
		Label lblPecentErrMean = new Label(9, 0, "Error mean (%)", boldCellFormat12);
		sheet.addCell(lblPecentErrMean);
		Label lblPercentErrSd = new Label(10, 0, "Error sd (%)", boldCellFormat12);
		sheet.addCell(lblPercentErrSd);
		
		Label lblPvalue = new Label(11, 0, "P-value", boldCellFormat12);
		sheet.addCell(lblPvalue);

		for (int i = 0; i < modelList.size(); i++) {
			RModel model = modelList.get(i);
			
			Label spec = null;
			String specInfo = RModelAssoc.prettySpec(model.getSpec());
			if (isChosen(model))
				spec = new Label(0, i + 1, specInfo, boldCellFormat12);
			else
				spec = new Label(0, i + 1, specInfo, cellFormat12);
			sheet.addCell(spec);
			
			Label type = new Label(1, i + 1, model.getType(), cellFormat12);
			sheet.addCell(type);

			Number fitness = new Number(2, i + 1, 
					MathUtil.round(model.getFitness()), cellFormat12);
			sheet.addCell(fitness);
			
			Number r = new Number(3, i + 1, 
					MathUtil.round(model.getR()), cellFormat12);
			sheet.addCell(r);

			Number ss = new Number(4, i + 1, 
					MathUtil.round(model.getSumOfSquares()), cellFormat12);
			sheet.addCell(ss);

			Number mean = new Number(5, i + 1, 
					MathUtil.round(model.getMean()), cellFormat12);
			sheet.addCell(mean);

			Number sd = new Number(6, i + 1, 
					MathUtil.round(model.getSd()), cellFormat12);
			sheet.addCell(sd);

			Number errMean = new Number(7, i + 1, 
					MathUtil.round(model.getErrMean()), cellFormat12);
			sheet.addCell(errMean);

			Number errSd = new Number(8, i + 1, 
					MathUtil.round(model.getErrSd()), cellFormat12);
			sheet.addCell(errSd);

			Label ratioErrMean = new Label(9, i + 1, 
					MathUtil.round(model.getRatioErrMean() * 100.0) + "%", cellFormat12);
			sheet.addCell(ratioErrMean);

			Label ratioErrSd = new Label(10, i + 1, 
					MathUtil.round(model.getRatioErrSd() * 100.0) + "%", cellFormat12);
			sheet.addCell(ratioErrSd);

			Label pvalue = new Label(11, i + 1, 
					MathUtil.format(model.getPvalue()), cellFormat12);
			sheet.addCell(pvalue);
		}
		
		
		workbook.write();
		workbook.close();
	}
	
	
	public void copyToClipboard(boolean onlySpec) {
		if (!Config.testClipboard()) {
			return;
		}
		
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < size(); i++) {
			RModel model = get(i);
			if ( i > 0)
				buffer.append("\n");
			
			String text = "";
			if (onlySpec)
				text = RModelAssoc.prettySpec(model.getSpec());
			else
				text = model.toString();
					
			buffer.append(text);
		}
		
		Config.getClipboardProcessor().setText(buffer.toString());
	}

	
	public RModel get(int idx) {
		return modelList.get(idx);
	}
	
	
	@Override
	public boolean addAll(Collection<? extends RModel> c) {
		return modelList.addAll(c);
	}


	@Override
	public void clear() {
		modelList.clear();
		clearChosenList();
	}


	@Override
	public boolean contains(Object o) {
		return modelList.contains(o);
	}


	@Override
	public boolean containsAll(Collection<?> c) {
		return modelList.containsAll(c);
	}


	@Override
	public boolean isEmpty() {
		return modelList.isEmpty();
	}


	@Override
	public Iterator<RModel> iterator() {
		return modelList.iterator();
	}


	@Override
	public boolean remove(Object o) {
		if (o instanceof RModel)
			removeChosen((RModel)o);
		
		return modelList.remove(o);
	}


	@Override
	public boolean removeAll(Collection<?> c) {
		for (Object o : c) {
			if (o instanceof RModel) {
				removeChosen((RModel)o);
			}
		}
		return modelList.removeAll(c);
	}


	@Override
	public boolean retainAll(Collection<?> c) {
		ArrayList<RModel> list = 
			new ArrayList<RModel>(this.modelList);
		
		boolean result = modelList.retainAll(c);
		
		for (RModel model : list) {
			if (!this.modelList.contains(model))
				removeChosen(model);
		}
		
		return result;
	}


	@Override
	public int size() {
		return modelList.size();
	}


	@Override
	public Object[] toArray() {
		return modelList.toArray();
	}


	@Override
	public <T> T[] toArray(T[] a) {
		return modelList.toArray(a);
	}


	@Override
	public boolean add(RModel e) {
		return modelList.add(e);
	}


	@Override
	public void add(int index, RModel element) {
		modelList.add(index, element);
	}


	@Override
	public boolean addAll(int index, Collection<? extends RModel> c) {
		return modelList.addAll(index, c);
	}


	@Override
	public int indexOf(Object o) {
		return modelList.indexOf(o);
	}

	
	@Override
	public int lastIndexOf(Object o) {
		return modelList.lastIndexOf(o);
	}


	@Override
	public ListIterator<RModel> listIterator() {
		return modelList.listIterator();
	}


	@Override
	public ListIterator<RModel> listIterator(int index) {
		return modelList.listIterator(index);
	}


	@Override
	public RModel remove(int index) {
		RModel model = modelList.get(index);
		removeChosen(model);
		return modelList.remove(index);
	}


	@Override
	public RModel set(int index, RModel element) {
		RModel old = modelList.get(index);
		if (!old.equals(element))
			removeChosen(old);
		return modelList.set(index, element);
	}

	
	@Override
	public List<RModel> subList(int fromIndex, int toIndex) {
		return modelList.subList(fromIndex, toIndex);
	}
	
	

}
