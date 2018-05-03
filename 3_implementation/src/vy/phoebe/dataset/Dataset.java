package vy.phoebe.dataset;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;
import vy.phoebe.util.DSUtil;
import vy.phoebe.util.FileUtil;
import vy.phoebe.util.MinMax;

import com.csvreader.CsvReader;

import flanagan.analysis.Stat;

public class Dataset {

	protected List<String> measureNameList = new ArrayList<String>();
	
	
	protected List<double[]> data = DSUtil.newList();

	
	private Dataset() {
		
	}
	
	
	public double[] getRowArray(int row) {
		return data.get(row);
	}
	
	
	public int getRows() {
		return data.size();
	}
	
	
	public Vector<Double> getRowVector(int row) {
		Vector<Double> vector = new Vector<Double>();
		
		double[] rowData = data.get(row);
		for (double value : rowData) {
			vector.add(new Double(value));
		}
		
		return vector;
	}
	
	
	public Vector<Object> getObjectRowVector(int row) {
		Vector<Object> vector = new Vector<Object>();
		
		double[] rowData = data.get(row);
		for (double value : rowData) {
			vector.add(new Double(value));
		}
		
		return vector;
	}

	
	public Vector<Vector<Double>> getDataVector() {
		Vector<Vector<Double>> vData = new Vector<Vector<Double>>();
		
		for (int i = 0; i < data.size(); i++) {
			Vector<Double> vector = getRowVector(i);
			vData.add(vector);
		}
		
		return vData;
	}

	
	public double getMeasure(int row, int col) {
		return data.get(row)[col];
	}

	
	public double getMeasure(int row, String measure) {
		int col = measureNameList.indexOf(measure);
		return data.get(row)[col];
	}
	
	
	public boolean contains(String measure) {
		return (measureNameList.indexOf(measure) != -1);
	}
	
	
	public boolean containsAll(Collection<String> measureList) {
		for (String measure : measureList) {
			if (!contains(measure))
				return false;
		}
		
		return true;
	}

	
	public List<Double> getMeasureList(int idx) {
		List<Double> measures = DSUtil.newList();
		
		for (double[] list :  data) {
			measures.add(list[idx]);
		}
		
		return measures;
	}

	
	public int getMeasureIndexOf(String measure) {
		return measureNameList.indexOf(measure);
	}
	
	
	public double[] getMeasureArray(int idx) {
		double[] measures = new double[data.size()];
		
		for (int i = 0; i < data.size(); i++) {
			double[] list =  data.get(i);
			measures[i] = list[idx];
		}
		
		return measures;
	}
	
	
	public double[] getMeasureSortedArray(int idx) {
		double[] array = getMeasureArray(idx);
		Set<Double> set = new HashSet<Double>();
		for (double v : array) {
			set.add(v);
		}
		List<Double> list = new ArrayList<Double>();
		list.addAll(set);
		Collections.sort(list);
		
		return DSUtil.toArray(list);
	}

	
	public double[] getMeasureArrayByName(String measure) {
		int idx = measureNameList.indexOf(measure);
		return getMeasureArray(idx);
	}
	
	
	public double[] getMeasureSortedArrayByName(String measure) {
		int idx = measureNameList.indexOf(measure);
		return getMeasureSortedArray(idx);
	}
	
	
	public double[][] getMeasureMatrix(List<String> measureList) {
		double[][] data = new double[measureList.size()][];
		for (int i = 0; i < measureList.size(); i++)
			data[i] = getMeasureArrayByName(measureList.get(i));
		
		return data;
	}
	
	
	public ArrayList<String> getMeasureNameList() {
		return new ArrayList<String>(this.measureNameList);
	}
	
	
	public Vector<String> getMeasureNameVector() {
		return new Vector<String>(this.measureNameList);
	}

	
	public boolean isEmpty() {
		return (data.size() == 0);
	}
	
	
	public MinMax getMeasureRange(String measure) {
		if (!contains(measure))
			return null;
			
		double[] data = getMeasureArrayByName(measure);
		Stat stat = new Stat(data);
		return new MinMax(stat.minimum(), stat.maximum());
		
	}
	
	
	public void clear() {
		data.clear();
		measureNameList.clear();
	}
	
	
	public static Dataset parse(File file) {
		Dataset dataset = null;
		String ext = FileUtil.getExtension(file);
		if (ext != null && ext.toLowerCase().equals("xls")) {
			dataset = parseExcel(file);
		}
		else
			dataset = parseCSV(file);
		
		return dataset;
	}

	
	protected static Dataset parseExcel(File file) {
		Workbook workbook = null;
		Dataset dataset = null;
		
		try {
			workbook = Workbook.getWorkbook(file);
			dataset = parseExcel(workbook);
			workbook.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
			if (dataset != null) {
				dataset.clear();
				dataset = null;
			}
		}
		finally {
			if (workbook != null)
				workbook.close();
		}
		
		return dataset;
	}
	
	
	protected static Dataset parseExcel(Workbook workbook) {
		List<String> measureNameList = new ArrayList<String>();
		List<double[]> data = DSUtil.newList();
		
		Sheet sheet = workbook.getSheet(0);
		int columns = sheet.getColumns();
		
		for (int j = 0; j < columns; j++) {
			Cell cell = sheet.getCell(j, 0);
			CellType type = cell.getType();
			if (type == CellType.EMPTY || type == CellType.ERROR)
				break;
			String measureName = cell.getContents(); 
			measureNameList.add(measureName);
		}
		if (measureNameList.size() < 2)
			return null;
		
		columns = measureNameList.size();
		int rows = sheet.getRows();
		for (int i = 1; i < rows; i++) {
			double[] row = new double[columns];
			for (int j = 0; j < columns; j++) {
				Cell cell = sheet.getCell(j, i);
				CellType type = cell.getType();
				if (type == CellType.EMPTY || type == CellType.ERROR) {
					row = null;
					break;
				}
				
				try {
					double value = Double.parseDouble(cell.getContents());
					row[j] = value;
				}
				catch (Throwable e) {
					e.printStackTrace();
					row = null;
					break;
				}
			}
			if(row != null)
				data.add(row);
		}
		
		if (data.size() == 0) return null;
		
		Dataset dataset = new Dataset();
		dataset.measureNameList = measureNameList;
		dataset.data = data;
		return dataset;
	}
	
	
	protected static Dataset parseCSV(File file) {
		List<String> measureNameList = new ArrayList<String>();
		List<double[]> data = DSUtil.newList();
		
		CsvReader csvReader = null;
		try {
			csvReader = new CsvReader(file.getAbsolutePath());
			csvReader.readHeaders();
			String[] headers = csvReader.getHeaders();
			measureNameList.addAll(Arrays.asList(headers));
			if (measureNameList.size() < 2) {
				csvReader.close();
				return null;
			}
			
			while (csvReader.readRecord()) {
				String[] values = csvReader.getValues();
				if (values.length < measureNameList.size())
					continue;
				
				double[] row = new double[measureNameList.size()];
				for (int i = 0; i < row.length; i++) {
					try {
						double value = Double.parseDouble(values[i]);
						row[i] = value;
					}
					catch (Throwable e) {
						e.printStackTrace();
						row = null;
						break;
					}
				}
				
				if (row != null)
					data.add(row);
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			if (csvReader != null)
				csvReader.close();
		}
		
		if (data.size() == 0) return null;
		
		Dataset dataset = new Dataset();
		dataset.measureNameList = measureNameList;
		dataset.data = data;
		return dataset;
	}
	

	
}
