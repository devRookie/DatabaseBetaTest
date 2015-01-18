package com.devrookie.databasebeta;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

    DBAdapter myDb;

    //Layout Variables
    Button btnAddVariable, btnClearVariable, btnSearchVariable;
    EditText searchText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find EditText
        this.searchText = (EditText)findViewById(R.id.search_text);

        //Find Button ID
        this.btnAddVariable = (Button) findViewById(R.id.btnAdd);
        this.btnClearVariable = (Button) findViewById(R.id.btnClear);
        this.btnSearchVariable = (Button) findViewById(R.id.btnSearch);

//        this.myArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, myArray);//Configure Adapter
//        this.lvFromDB.setAdapter(myArrayAdapter);

//        searchA = new SearchListAdapter(MainActivity.this, myArray);
//        this.lvFromDB.setAdapter(searchA);

        registerListLongClickCallback();//**** Long Click
        registerListClickCallBack(); //**** Single Click

        //Add button with dialog
        this.btnAddVariable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog(0);
            }
        });

        //Open SQL database
        openDB();
        populateListViewFromDB();

    }

    private void openDB() {
        myDb = new DBAdapter(this);
        myDb.open();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDB();
    }

    private void closeDB() {
        myDb.close();
    }

    //Edited Add Record
    public void onClick_AddRecord(String title, String description) {

        //insertRow uses 2 parameters..title description
        myDb.insertRow(title, description);

        Toast.makeText(this, "Successfully added: " + title, Toast.LENGTH_SHORT).show();
        populateListViewFromDB();
    }

    public void onClick_Refresh(View v){
        populateListViewFromDB();
    }

    //TODO: Clears database
    public void onClick_ClearAll(View v) {
        final AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
        alert.setTitle("WARNING!")
                .setMessage("Are you sure you want to clear all list?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        myDb.deleteAll();
                        populateListViewFromDB();
                    }//onCLickPositive
                })
                .setNegativeButton("No, GO BACK!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }//onClickNegative
                }).show();
    }//onClick_CLearAll


    //Dialog when ADD is clicked
    protected Dialog onCreateDialog(int id) {
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.dialog_add_layout, null);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setView(dialogView) //Sets view to dialog_add_layout
                .setTitle("Add new note")
                .setMessage("Please do not leave any empty entry.")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        EditText etTitle = (EditText) dialogView.findViewById(R.id.etTitle); //dialogView = dialog_add_layout Layout XML
                        EditText etDesc = (EditText) dialogView.findViewById(R.id.etDes);
                        String title = etTitle.getText().toString();
                        String description = etDesc.getText().toString();
                        //Title
                        if (title.equals("")){
                            Toast.makeText(MainActivity.this, "Please add a title", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Description
                        if (description.equals("")){
                            Toast.makeText(MainActivity.this, "Please add a description", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        //Now Add it all to the database.
                        onClick_AddRecord(title, description);
                        //Clear text after success of adding
                        etTitle.getText().clear();
                        etDesc.getText().clear();
                    }//onClick
                }//positiveButton
                )//positiveButton
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.cancel();
                            }
                        });
        return alert.create();
    }//onCreateDialog



    public void onClick_Search(View v) {
        String search = this.searchText.getText().toString();

        //Do nothing, since they can refresh to get all data again.
        if (search.equals("")){
            return;
        }//ifStatment

        Cursor cursor = myDb.getMatchRows(search);
        startManagingCursor(cursor);

        String[] fromFieldNames = new String[]
                {DBAdapter.KEY_TITLE, DBAdapter.KEY_DESCRIPTION};
        int[] toViewIDs = new int[]
                {R.id.item_title,
                        R.id.item_description};

        // Create adapter to match columns of the DB onto element in the UI.
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,		// Context
                        R.layout.activity_list_view,	// Row layout template
                        cursor,					// cursor (set of DB records to map)
                        fromFieldNames,			// DB Column names
                        toViewIDs				// View IDs to put information in
                );

        // Set the adapter for the list view
        ListView myList = (ListView) findViewById(R.id.listViewFromDB);
        myList.setAdapter(myCursorAdapter);
    }//onClickSearch

    private void populateListViewFromDB() {
        Cursor cursor = myDb.getAllRows();
        startManagingCursor(cursor); // startManagingCursorr

        String[] fromFieldNames = new String[]
                {DBAdapter.KEY_TITLE, DBAdapter.KEY_DESCRIPTION};
        int[] toViewIDs = new int[] {R.id.item_title, R.id.item_description};

        // Create adapter to match columns of the DB onto element in the UI.
        SimpleCursorAdapter myCursorAdapter =
                new SimpleCursorAdapter(
                        this,		// Context
                        R.layout.activity_list_view,	// Row layout template
                        cursor,					// cursor (set of DB records to map)
                        fromFieldNames,			// DB Column names
                        toViewIDs				// View IDs to put information in
                );

        // Set the adapter for the list view
        ListView myList = (ListView) findViewById(R.id.listViewFromDB);
        myList.setAdapter(myCursorAdapter);
    }

    //LongClick
    private void registerListLongClickCallback() {
        ListView myList = (ListView) findViewById(R.id.listViewFromDB);
        myList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View viewClicked, int position, final long id) {
                //long id = ID in database
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Delete List")
                        .setMessage("Are you sure you want to delete this record?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDb.deleteRow(id);
                                Toast.makeText(getApplicationContext(), "Deleted Successfully", Toast.LENGTH_SHORT).show();
                                populateListViewFromDB();
                            }//onClick
                        })//positiveButton
                        .setNegativeButton("No, Go back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })//NegativeButton
                        .show();
                return true;
            }//onItemLongClick
        });//myList.setOnItemLongClickListener
    }//registerListCLickCallBack

    //For single click
    private void registerListClickCallBack() {
        ListView myList = (ListView) findViewById(R.id.listViewFromDB);
        myList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, final long id) {

                //TODO: Getting text
                TextView titleText = (TextView)view.findViewById(R.id.item_title);
                TextView descriptionText = (TextView)view.findViewById(R.id.item_description);
                final String setTitle = titleText.getText().toString();
                //TODO: Replacing line breaks so it wont be all in one line
                final String setDescription = descriptionText.getText().toString().replaceAll("\\n", "\n");

                //Popup View Mode
                LayoutInflater viewInflate = getLayoutInflater();
                View viewMode = viewInflate.inflate(R.layout.dialog_view_mode, null);
                AlertDialog.Builder viewAlert = new AlertDialog.Builder(MainActivity.this);
                viewAlert.setTitle("View Mode")
                        .setView(viewMode);

                //TODO: Setting Dialog Text IN VIEW MODE AKA TEXT VIEW not EDIT TEXT
                final TextView viewTitle = (TextView) viewMode.findViewById(R.id.tvTitle);
                final TextView viewDescription = (TextView) viewMode.findViewById(R.id.tvDes);
                viewTitle.setText(setTitle);
                viewDescription.setText(setDescription);

                viewAlert.setPositiveButton("Edit", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //TODO: EDIT MODE:::: First inflate the view, then get the text from ListView and set to EditText
                        LayoutInflater inflater = getLayoutInflater();
                        View dialogView = inflater.inflate(R.layout.dialog_add_layout, null);
                        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                        alert.setView(dialogView);
                        alert.setTitle("Edit Mode");

                        //ToDO: Set up the text now into the Edit Mode
                        final EditText etTitleV = (EditText) dialogView.findViewById(R.id.etTitle);
                        final EditText etDesV = (EditText) dialogView.findViewById(R.id.etDes);
                        etTitleV.setText(setTitle);
                        etDesV.setText(setDescription);

                        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(getApplicationContext(), "Edited successfully.", Toast.LENGTH_SHORT).show();
                                String newTitle = etTitleV.getText().toString();
                                String newDescription = etDesV.getText().toString();
                                myDb.updateRow(id, newTitle, newDescription);
                                populateListViewFromDB();
                            }
                        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show(); // } = NegativeButton **** ) = "Cancel, new etc...)
                    }//onClick
                }).setNegativeButton("Return", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }//onClickNegativeOuter
                }).show();//NegativeBracket / Return Parenthesis / Show
            }//on Item Selected NOT dialog option pick
        });//myLIstOnClick / Register the list items
    }//register


}// **** TODO: THIS IS MAIN ACTIVITY END OF LINE
