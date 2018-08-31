Please run the script file titled run.sh to run the code.

Language Used: Java

Approach:

The intial data from actual and predicted files is stored in a List of type "RecordFormat". RecordFormat is a class (custom data type) consisting of time (int), stock (String) and price (double). The time, stock and price values are segregated using pipe delimiter.

A two dimentional List of List called sumList is created. Each record(row) corresponds to a distinct time in chronological order. It contains information about the stocks which are present in both actual and predicted data files at a particular time.
The first column contains the time, second column contains the sum of error between the actual and predicted values of stocks present in both actual and predicted files at that particular time. The third column contains the total number of common stocks present in both the files at that particlar time. Then the avg error for any particular hour can be calculated by dividing the sum (value in second column) by the total number of occurances (value in third column).

A two dimentional table called Comparison Table is created using the above mentioned sumList. This table utilizes the information about the window size. Each record(row) corresponds to a particlar window. It contains three column, first column contains the start time of a given window, second column contains the end time of that window and the third column contains the average error of that given window. Each record/row of comparison table is created using the sumList by adding the errors (second column of sumList) corresponding to each distict time(record/row in sumList) in the window and then dividing it by the total number of occurances (third column of sumList) in the window frame.

The output file is generated using the Comparison Table.
