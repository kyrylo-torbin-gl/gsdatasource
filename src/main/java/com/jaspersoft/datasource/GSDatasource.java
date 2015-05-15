package com.jaspersoft.datasource;

import com.google.gdata.client.GoogleAuthTokenFactory;
import com.google.gdata.client.spreadsheet.SpreadsheetService;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.util.AbstractSampleApp;
import org.apache.axis.transport.http.HTTPTransport;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

/**
 * Created by artem.shevchuk on 5/14/2015.
 */
public class GSDatasource extends AbstractSampleApp {

    String USERNAME = "NAME";
    String PASSWORD = "PASS";

    public static void main(String[] args) throws IOException, JRException {

        main(new GSDatasource(), new String[]{"test"});
    }

    @Override
    public void test() throws JRException {
        try {
            SpreadsheetService service = new SpreadsheetService("GSDatasource");
            authorize(service);


            // Define the URL to request.  This should never change.
            URL SPREADSHEET_FEED_URL = null;
            SPREADSHEET_FEED_URL = new URL(
                    "https://spreadsheets.google.com/feeds/spreadsheets/private/full");
            SpreadsheetFeed feed = null;
            feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
            SpreadsheetEntry entry = getSpreadsheet(feed,"test");
            List<WorksheetEntry> worksheets = entry.getWorksheets();
            if(worksheets.size() > 0){
                WorksheetEntry worksheet = worksheets.get(0);

                // Fetch the list feed of the worksheet.
                URL listFeedUrl = worksheet.getListFeedUrl();
                ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

                // Get header name
                ListEntry listEntry = listFeed.getEntries().get(0);
                CustomElementCollection header = listEntry.getCustomElements();
                for (String tag : header.getTags()) {
                    System.out.print(tag + "\t");
                }
                System.out.println();
                // Iterate through each row, printing its cell values.
                for (ListEntry row : listFeed.getEntries()) {
                    // Iterate over the remaining columns, and print each cell value
                    CustomElementCollection customElements = row.getCustomElements();
                    for (String tag : customElements.getTags()) {
                        System.out.print(customElements.getValue(tag) + "\t");
                    }
                    System.out.println();
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (AuthenticationException e) {
            e.printStackTrace();
        } catch (ServiceException e) {
            e.printStackTrace();
        }

        // Make a request to the API and get all spreadsheets.
    }

    public SpreadsheetEntry getSpreadsheet(SpreadsheetFeed feed, String name){
        SpreadsheetEntry result = null;
        List<SpreadsheetEntry> spreadsheets = feed.getEntries();

        // Iterate through all of the spreadsheets returned
        for (SpreadsheetEntry spreadsheet : spreadsheets) {
            if(spreadsheet.getTitle().getPlainText().equals(name)){
                result = spreadsheet;
                break;
            }
            //             Print the title of this spreadsheet to the screen;
            System.out.println(spreadsheet.getTitle().getPlainText());
        }
        return result;
    }

    public void authorize(SpreadsheetService service) throws AuthenticationException {
        service.setUserCredentials(USERNAME, PASSWORD);
    }
}
