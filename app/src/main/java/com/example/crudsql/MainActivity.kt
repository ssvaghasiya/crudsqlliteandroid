package com.example.crudsql

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    companion object {
        val DATABASE_NAME = "myemployeedatabase"
    }

    var textViewViewEmployees: TextView? = null
    var editTextName: EditText? = null
    var editTextSalary: EditText? = null
    var spinnerDepartment: Spinner? = null

    var mDatabase: SQLiteDatabase? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textViewViewEmployees = findViewById(R.id.textViewViewEmployees) as TextView
        editTextName = findViewById(R.id.editTextName) as EditText
        editTextSalary = findViewById(R.id.editTextSalary) as EditText
        spinnerDepartment = findViewById(R.id.spinnerDepartment) as Spinner

        findViewById<Button>(R.id.buttonAddEmployee).setOnClickListener {
            addEmployee()
        }
        textViewViewEmployees!!.setOnClickListener {
            startActivity(Intent(this, EmployeeActivity::class.java))
        }

        //creating a database
        mDatabase = openOrCreateDatabase(DATABASE_NAME, MODE_PRIVATE, null);
    }

    //this method will validate the name and salary
//dept does not need validation as it is a spinner and it cannot be empty
    private fun inputsAreCorrect(name: String, salary: String): Boolean {
        if (name.isEmpty()) {
            editTextName!!.error = "Please enter a name"
            editTextName!!.requestFocus()
            return false
        }
        if (salary.isEmpty() || salary.toInt() <= 0) {
            editTextSalary!!.error = "Please enter salary"
            editTextSalary!!.requestFocus()
            return false
        }
        return true
    }

    //this method will create the table
//as we are going to call this method everytime we will launch the application
//I have added IF NOT EXISTS to the SQL
//so it will only create the table when the table is not already created
    private fun createEmployeeTable() {
//        mDatabase!!.execSQL(
//            " CREATE TABLE IF NOT EXISTS employees (" +
//                    "id" + " int PRIMARY KEY, " +
//                    "name" + " varchar(200) NOT NULL, " +
//                    "department" + " varchar(200) NOT NULL, " +
//                    "joiningdate" + " datetime NOT NULL, " +
//                    "salary" + " double NOT NULL);"
//        );

        mDatabase!!.execSQL(
            "create table if not exists employees " +
                    "(id integer primary key autoincrement, name text,department text,joiningdate datetime,salary double)"
        )
    }


    private fun addEmployee() {
        createEmployeeTable()
        val name = editTextName!!.text.toString().trim { it <= ' ' }
        val salary = editTextSalary!!.text.toString().trim { it <= ' ' }
        val dept = spinnerDepartment!!.selectedItem.toString()

        //getting the current time for joining date
        val cal: Calendar = Calendar.getInstance()
        val sdf = SimpleDateFormat("yyyy-mm-dd hh:mm:ss")
        val joiningDate: String = sdf.format(cal.getTime())

        //validating the inptus
        if (inputsAreCorrect(name, salary)) {
            val insertSQL = """
                INSERT INTO employees 
                (name, department, joiningdate, salary)
                VALUES 
                (?, ?, ?, ?);
                """.trimIndent()

            //using the same method execsql for inserting values
            //this time it has two parameters
            //first is the sql string and second is the parameters that is to be binded with the query
            var a = mDatabase!!.execSQL(insertSQL, arrayOf(name, dept, joiningDate, salary))
            Toast.makeText(this, "Employee Added Successfully " + a, Toast.LENGTH_SHORT).show()
        }
    }

}