package util;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.*;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GoogleSheetsUtil {

    private static final String APPLICATION_NAME = "MyApp";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    public static final String credentialsPath = "/json/sheet_credentials.json";
    public static final String viewerSheetId = "1cktT79ohxoIhijc1OpP7zK3k7IYLMPPW9YhjYPZpQrY";
    public static final String databaseSheetId= "1xrylmB5h3mSFtaPvmhH8Kze1NWoBzk3_AAC36wPjuvs";

    private Sheets sheetsService;
    private List<Request> requests = new ArrayList<>();
    private BatchUpdateSpreadsheetRequest batchRequest;

    public GoogleSheetsUtil() throws IOException, GeneralSecurityException {
        sheetsService = createSheetsService();
    }

    private Sheets createSheetsService() throws IOException, GeneralSecurityException {
        HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
        GoogleCredential credential = GoogleCredential
                .fromStream(new ClassPathResource(credentialsPath).getInputStream())
                .createScoped(Arrays.asList(SheetsScopes.SPREADSHEETS));

        return new Sheets.Builder(httpTransport, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    /**
     * Append a row of data to a specific spreadsheet and sheet.
     * @param spreadsheetId Google Sheets ID (จาก URL)
     * @param sheetName ชื่อ Sheet (เช่น "Sheet1")
     * @param rowData ข้อมูล 1 แถว เช่น List ของ String หรือ Object ที่แปลงเป็น String
     */
    public void appendRow(String spreadsheetId, String sheetName, List<Object> rowData) throws IOException {
        ValueRange appendBody = new ValueRange()
                .setValues(Arrays.asList(rowData));

        AppendValuesResponse response = sheetsService.spreadsheets().values()
                .append(spreadsheetId, sheetName, appendBody)
                .setValueInputOption("RAW")
                .setInsertDataOption("INSERT_ROWS") // ใส่แถวใหม่
                .setIncludeValuesInResponse(true)  // ให้ API ส่งข้อมูลที่เพิ่มกลับมา
                .execute();

        System.out.println("Update response: " + response);
    }

    public void clearSheet(String spreadsheetId, String sheetName) throws IOException {
        ClearValuesRequest requestBody = new ClearValuesRequest();

        sheetsService.spreadsheets().values()
                .clear(spreadsheetId, sheetName, requestBody)
                .execute();
    }

    public void updateRange(String spreadsheetId, String range, List<List<Object>> values) throws IOException {
        // แยกชื่อชีตจาก range เช่น "Sheet1!A1:B2" -> "Sheet1"
        String sheetName = range.contains("!") ? range.substring(0, range.indexOf('!')) : range;
        if (sheetName.startsWith("'") && sheetName.endsWith("'")) {
            sheetName = sheetName.substring(1, sheetName.length() - 1);
        }
        // ดึงข้อมูล Spreadsheet เพื่อเช็คว่าชีตมีหรือไม่
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();

        boolean sheetFound = false;
        for (Sheet sheet : spreadsheet.getSheets()) {
            String actualTitle = sheet.getProperties().getTitle();
            if (actualTitle.equals(sheetName.replace("''", "'"))) { // เผื่อกรณี escape มาเป็น double quote
                sheetFound = true;
                break;
            }
        }


        if (!sheetFound) {
            return; // ข้าม ไม่โยน exception
        }

        // ถ้ามีชีตค่อยอัพเดต
        ValueRange body = new ValueRange().setValues(values);
        sheetsService.spreadsheets().values()
                .update(spreadsheetId, range, body)
                .setValueInputOption("RAW")
                .execute();
    }

    public void clearRange(String spreadsheetId, String range) throws IOException {
        ClearValuesRequest requestBody = new ClearValuesRequest();

        sheetsService.spreadsheets().values()
                .clear(spreadsheetId, range, requestBody)
                .execute();
    }

    public void takeRequest(Request request) {
        requests.add(request);
    }

    public void takeRequests(List<Request> requests) {
        this.requests.addAll(requests);
    }

    public void requestClear() {
        requests.clear();
    }

    public void requestSet() {
        batchRequest = new BatchUpdateSpreadsheetRequest().setRequests(requests);
    }

    public void processRequest(String spreadSheetId) {
        try {
            sheetsService.spreadsheets().batchUpdate(spreadSheetId, batchRequest).execute();
        } catch (GoogleJsonResponseException e) {
            System.err.println("Google API error: " + e.getDetails());
        } catch (IOException e) {
            System.err.println("Network หรือ IO error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
        }
    }

    public static Request buildUpdateCellsRequest(int sheetId, String rangeA1, List<List<Object>> values) {
        // แปลง A1 → row/column index
        GridCoordinate start = convertA1RangeToGridCoordinate(rangeA1, sheetId);

        List<RowData> rows = new ArrayList<>();
        for (List<Object> row : values) {
            List<CellData> cells = new ArrayList<>();
            for (Object val : row) {
                ExtendedValue extVal = new ExtendedValue();
                if (val instanceof String) {
                    extVal.setStringValue((String) val);
                } else if (val instanceof Number) {
                    extVal.setNumberValue(((Number) val).doubleValue());
                } else if (val instanceof Boolean) {
                    extVal.setBoolValue((Boolean) val);
                }
                cells.add(new CellData().setUserEnteredValue(extVal));
            }
            rows.add(new RowData().setValues(cells));
        }

        UpdateCellsRequest update = new UpdateCellsRequest()
                .setStart(start)
                .setRows(rows)
                .setFields("userEnteredValue");

        return new Request().setUpdateCells(update);
    }

    private static GridCoordinate convertA1RangeToGridCoordinate(String rangeA1, int sheetId) {
        // ดึงตำแหน่งเริ่มต้นจาก A1 เช่น "Status!B2" → B = 1, 2 = 1 (index base 0)
        String[] parts = rangeA1.split("!");
        String a1 = parts.length == 2 ? parts[1] : parts[0];
        String cell = a1.split(":")[0]; // B2 จาก B2:D4

        String columnPart = cell.replaceAll("[^A-Z]", "");
        String rowPart = cell.replaceAll("[^0-9]", "");

        int col = columnNameToIndex(columnPart);
        int row = Integer.parseInt(rowPart) - 1;

        return new GridCoordinate()
                .setSheetId(sheetId)
                .setRowIndex(row)
                .setColumnIndex(col);
    }

    private static int columnNameToIndex(String col) {
        int result = 0;
        for (int i = 0; i < col.length(); i++) {
            result *= 26;
            result += col.charAt(i) - 'A' + 1;
        }
        return result - 1; // แปลง A=0
    }

    public Integer getSheetIdByName(String spreadsheetId, String sheetName) throws IOException {
        Spreadsheet spreadsheet = sheetsService.spreadsheets().get(spreadsheetId).execute();
        List<Sheet> sheets = spreadsheet.getSheets();

        for (Sheet sheet : sheets) {
            SheetProperties props = sheet.getProperties();
            if (props.getTitle().equals(sheetName)) {
                return props.getSheetId();
            }
        }

        return null;
    }

    public static String escapeSheetName(String name) {
        return "'" + name.replace("'", "''") + "'";
    }

}
