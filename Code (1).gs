/**
 * Author: Karthik Sreedhar
 * Searches for emails from excel file in
 * organizational directory & outputs in
 * tabular format in google documents
 * REPLACE: placeholders that require modification
 * - Input_Spreadsheet_Name: name of spreadsheet
 * - Input_SpreadSheet_ID: ID of spreadhsheet
 * - Output_Document_Name: name of output doc
 */

function listUsers() {
  
  // initialize variables to read in spreadsheet
  var FileIterator = DriveApp.getFilesByName('REPLACE: Input_Spreadsheet_Name');
  var id = "REPLACE: Input_Spreadsheet_ID";

  // iterate through potential files
  while (FileIterator.hasNext())
  { 

    var file = FileIterator.next();

    // if appropriate file is reached
    if (file.getName() == 'REPLACE: Input_Spreadsheet_Name')
    {

      var emails = [];
      var v = Sheets.Spreadsheets.Values.get(id, 'A2:A').values;
      
      // add names & emails to empty list from spreadsheet
      for(var index = 0; index < v.length; index++) {

        var name = v[index][0];
        var email = getEmail(name);
        emails.push([name, email]);
        Logger.log([name, email])
        
      }
    }    
  }

  // create google document & add information in tabular format
  var doc = DocumentApp.create('REPLACE: Output_Document_Name');
  var body = doc.getBody();
  body.insertParagraph(0, doc.getName())
    .setHeading(DocumentApp.ParagraphHeading.HEADING1);
  table = body.appendTable(emails);

}


// function that searches directory & returns email given name
function getEmail(input) {

  // timeout to avoid over-request exception
  Utilities.sleep(5000)

  // create variable containing all information regarding person from directory
  var p = People.People.searchDirectoryPeople({query: input, readMask: 'emailAddresses', sources: 2});
    
  // identify error cases and return "Invalid"
  if (p.people == null) { return "Invalid"; }
  // identify email address from people object in directory 
  else { return p.people[0]['emailAddresses'][0]['value']; }

}