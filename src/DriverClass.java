import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class DriverClass {

	static String actualFile;
	static String predictedFile;
	static String windowFile;
	static String outputFile;	
	
	static List<RecordFormat> actual = new ArrayList<RecordFormat>();
	static List<RecordFormat> predicted = new ArrayList<RecordFormat>();
	static int windowSize;
	
	static double[][] summaryTable;
	static List<ArrayList<Double>> sumList = new ArrayList<ArrayList<Double>>();
	static List<Integer> time = new ArrayList<Integer>();
	static int predIndex = 0;
	static int compIndex = 0;
	static double[][] comparisonTable;
	
	public static void main(String[] args) {
		try {
			//Initialize input variables
			initializeInputVariables(args);
			readInputFile(actualFile, actual);
			readInputFile(predictedFile, predicted);
			readSize(windowFile);
			generateSumList();
			generateComparisonTable();
			generateOutputFile();
			//generateSummaryTable();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	public static void generateOutputFile() throws IOException{
		File outFile = new File(outputFile);
		if (!outFile.exists()) {
			outFile.createNewFile();
		}
		FileWriter fw = new FileWriter(outFile.getAbsoluteFile()); 
		BufferedWriter bw = new BufferedWriter(fw);
		
		try {
			for(int i = 0 ; i<comparisonTable.length ; i++){
				bw.write(new Double(comparisonTable[i][0]).intValue()
						+"|"+new Double(comparisonTable[i][1]).intValue()+"|");
				if(comparisonTable[i][2] == -1)
					bw.write("NA"+"\n");
				else
					bw.write(comparisonTable[i][2]+"\n");
			}
		} catch (IOException E) {
			E.printStackTrace();
		}
		
		
		bw.close();
		fw.close();
	}


	private static double findAverageError(int start, int end) {
		int i = compIndex;
		double sum = 0;
		double n = 0;
		double avgError;
		ArrayList<Double> record;
		
		for(int j = start; j<= end;j++){
			record = sumList.get(i);	
			if(record.get(0)==j){
				sum+=record.get(1);
				n+=record.get(2);
				i++;
			}
		}
		
		if(sumList.get(compIndex).get(0) == start)
			compIndex++;
		avgError = n!=0?sum/n:-1;
		return (double) Math.round(avgError * 100) / 100;
	}


	private static void generateComparisonTable() {
		
		int startHour = actual.get(0).time;
		int lastHour = actual.get(actual.size()-1).time;
		
		int lastWindowStart = lastHour - (windowSize-1);
		comparisonTable = new double[lastWindowStart-startHour+1][3];
		int start = startHour;
		int end=start+windowSize-1;
		int row = 0;
		while(start<=lastWindowStart){
			comparisonTable[row][0] = start;
			comparisonTable[row][1] = end;
			comparisonTable[row][2] = findAverageError(start,end);

			start++;end++;
			row++;
		}
		
	}


	private static void initializeInputVariables(String[] args) {
		actualFile = args[0];
		predictedFile = args[1];
		windowFile = args[2];
		outputFile = args[3];
		
	}
	
	private static void readInputFile(String inFileName, List<RecordFormat> outList) throws IOException{
		File inFile = new File(inFileName);
		FileReader fr = new FileReader(inFile);
		BufferedReader br=new BufferedReader(fr);
		String line = new String();
		//declare DS
		while ((line = br.readLine()) != null ){
			String[] lineSplit = line.split("\\|");
			int time = lineSplit[0]!=null?Integer.parseInt(lineSplit[0]):0;
			String stock = lineSplit[1]!=null?lineSplit[1]:null;
			double price = lineSplit[2]!=null?Double.parseDouble(lineSplit[2]):-1;
						
			RecordFormat record = new RecordFormat(time, stock, price);
			outList.add(record);
		}
		
		br.close();
		fr.close();
	}
	
	private static void readSize(String inputFile) throws IOException {
		File inFile = new File(inputFile);
		FileReader fr = new FileReader(inFile);
		BufferedReader br=new BufferedReader(fr);
		String line = br.readLine();
		windowSize = Integer.parseInt(line);
		br.close();
		fr.close();
		
	}
	
		
	private static void generateSumList(){
		RecordFormat record;
		int actualListIndex = 0;
		double diff;
		double predPrice;
		
		int prevTime = 0;
		double sum = 0;
		double n = 0;
		ArrayList<Double> list;
		
		while(actualListIndex<actual.size()){
			record = actual.get(actualListIndex);
			predPrice = findPredictedPrice(record.time, record.stock);
			actualListIndex++;
			
			if(predPrice == -1) 
				continue;
			
			else{
				diff = record.price>predPrice?record.price-predPrice:predPrice-record.price;
				diff = (double) Math.round(diff * 100) / 100;
				
				if(record.time!=prevTime){
					
					if(prevTime!=0){
					list = new ArrayList<Double>();
					list.add((double) prevTime);
					list.add((double) Math.round(sum * 100) / 100);
					list.add(n);
					
					sumList.add(list);
					}
					prevTime = record.time;
					sum = diff;
					n = 1;
				}
				else{
					sum = sum+diff;
					n = n+1;
				}
			}
		}
		
		if(prevTime!=0){
			list = new ArrayList<Double>();
			list.add((double) prevTime);
			list.add((double) Math.round(sum * 100) / 100);
			list.add(n);
			
			sumList.add(list);
			}	

	}
	
	private static void generateSummaryTable() {
		summaryTable = new double[predicted.size()][4];
		RecordFormat record;
		int actualListIndex = 0;
		int tableIndex = 0;
		double diff;
		double predPrice;
		
		while(actualListIndex<actual.size()){
			record = actual.get(actualListIndex);
			
			//find the corresponding stock in predicted table
			//a function that returns the predicted price of the stock if stock is found, else returns -1;
			predPrice = findPredictedPrice(record.time, record.stock);
			actualListIndex++;
			//if found then fill the entry in summary table
			if(predPrice == -1) 
				continue;
			else{
				summaryTable[tableIndex][0] = record.time;
				summaryTable[tableIndex][1] = record.price;
				summaryTable[tableIndex][2] = predPrice;
				diff = record.price>predPrice?record.price-predPrice:predPrice-record.price;
				summaryTable[tableIndex][3] = (double) Math.round(diff * 100) / 100;
				tableIndex++;
			}
		}
	}


	private static double findPredictedPrice(int time, String actualStock) {
		
		//get pred index will return the starting index from the predicted list corresponding to the time entered 
		int i = getPredIndex(time);
		RecordFormat predRecord = predicted.get(i);
		while(predRecord.time==time){
			if(predRecord.stock.equals(actualStock))
				return predRecord.price;
			i++;
			if(i<predicted.size())
				predRecord = predicted.get(i);
			else break;
		}
			
		return -1;
	}

	//use the chronological ordering 
	private static int getPredIndex(int time) {
		int predTime = predicted.get(predIndex).time;
		if(predicted.get(predIndex).time == time)
			return predIndex;
	
		while(predIndex<predicted.size() && predicted.get(predIndex).time!=time){
			predIndex++;
		}
		
		return predIndex;
	}
	

}
class RecordFormat {
	int time;
	String stock;
	double price;
	public RecordFormat(int time, String stock, double price) {
		super();
		this.time = time;
		this.stock = stock;
		this.price = price;
	}
	
	
}
