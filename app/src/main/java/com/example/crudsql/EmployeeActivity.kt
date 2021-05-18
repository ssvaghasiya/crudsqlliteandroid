package com.example.crudsql

import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity


class EmployeeActivity : AppCompatActivity() {

    var employeeList: ArrayList<Employee>? = null
    var mDatabase: SQLiteDatabase? = null
    var listViewEmployees: ListView? = null
    var adapter: EmployeeAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_employee)

        listViewEmployees =
            findViewById<View>(R.id.listViewEmployees) as ListView
        employeeList = ArrayList()

        //opening the database

        //opening the database
        mDatabase = openOrCreateDatabase(
            MainActivity.DATABASE_NAME,
            Context.MODE_PRIVATE,
            null
        )

        //this method will display the employees in the list

        //this method will display the employees in the list
        showEmployeesFromDatabase()
    }

    private fun showEmployeesFromDatabase() {

        //we used rawQuery(sql, selectionargs) for fetching all the employees
        val cursorEmployees: Cursor = mDatabase!!.rawQuery("SELECT * FROM employees", null)

        //if the cursor has some data
        if (cursorEmployees.moveToFirst()) {
            //looping through all the records
            do {
                //pushing each record in the employee list
                employeeList!!.add(
                    Employee(
                        cursorEmployees.getInt(0),
                        cursorEmployees.getString(1),
                        cursorEmployees.getString(2),
                        cursorEmployees.getString(3),
                        cursorEmployees.getDouble(4)
                    )
                )
            } while (cursorEmployees.moveToNext())
        }
        //closing the cursor
        cursorEmployees.close()

        //creating the adapter object
        adapter = EmployeeAdapter(this, R.layout.list_layout_employee, employeeList, mDatabase)

        //adding the adapter to listview
        listViewEmployees!!.adapter = adapter
    }
}