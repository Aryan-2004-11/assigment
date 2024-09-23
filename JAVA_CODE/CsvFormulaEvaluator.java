import java.io.*;
import java.util.*;
import java.util.regex.*;

public class CsvFormulaEvaluator {

    private static final String INPUT_CSV = "input.csv";
    private static final String OUTPUT_CSV = "output.csv";

    public static void main(String[] args) {
        try {
            String[][] data = readCsv(INPUT_CSV);
            String[][] evaluatedData = evaluateFormulas(data);
            writeCsv(OUTPUT_CSV, evaluatedData);
            System.out.println("Results have been written to " + OUTPUT_CSV);
        } catch (IOException e) {
            System.err.println("Error during file operations: " + e.getMessage());
        }
    }

    private static String[][] readCsv(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        List<String[]> rows = new ArrayList<>();
        String line;

        while ((line = reader.readLine()) != null) {
            rows.add(line.split(", "));
        }
        reader.close();

        return rows.toArray(new String[0][]);
    }

    private static String[][] evaluateFormulas(String[][] data) {
        String[][] results = new String[data.length][data[0].length];
        Map<String, String> cellValues = new HashMap<>();

        for (int row = 0; row < data.length; row++) {
            for (int col = 0; col < data[row].length; col++) {
                String cellContent = data[row][col].trim();
                if (cellContent.startsWith("=")) {
                    results[row][col] = String.valueOf(evaluateFormula(cellContent.substring(1), cellValues, data));
                } else {
                    results[row][col] = cellContent;
                    cellValues.put(getCellReference(row, col), cellContent);
                }
            }
        }
        return results;
    }

    private static double evaluateFormula(String formula, Map<String, String> cellValues, String[][] data) {
        // Replace cell references in the formula with their actual values
        Pattern cellPattern = Pattern.compile("[A-Z]+\\d+");
        Matcher matcher = cellPattern.matcher(formula);
        
        StringBuilder resolvedFormula = new StringBuilder(formula);
        while (matcher.find()) {
            String cellRef = matcher.group();
            String cellValue = cellValues.getOrDefault(cellRef, "0"); // Default to 0 if the reference is missing
            resolvedFormula = new StringBuilder(resolvedFormula.toString().replace(cellRef, cellValue));
        }

        return evaluateExpression(resolvedFormula.toString());
    }

    private static double evaluateExpression(String expression) {
        // Basic evaluation logic for addition and subtraction
        String[] additionTerms = expression.split("\\+");
        double total = 0;

        for (String term : additionTerms) {
            total += evaluateSubtraction(term);
        }

        return total;
    }

    private static double evaluateSubtraction(String term) {
        String[] subtractionTerms = term.split("-");
        double result = evaluateNumericValue(subtractionTerms[0].trim());

        for (int i = 1; i < subtractionTerms.length; i++) {
            result -= evaluateNumericValue(subtractionTerms[i].trim());
        }

        return result;
    }

    private static double evaluateNumericValue(String value) {
        try {
            return Double.parseDouble(value); // Convert the string value to a double
        } catch (NumberFormatException e) {
            // Return 0 for any invalid number formats
            return 0;
        }
    }

    private static void writeCsv(String filePath, String[][] data) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter(filePath));
        for (String[] row : data) {
            writer.write(String.join(", ", row));
            writer.newLine();
        }
        writer.close();
    }

    private static String getCellReference(int row, int col) {
        return String.valueOf((char) ('A' + col)) + (row + 1); // Convert numeric indices to cell reference format (e.g., A1)
    }
}
