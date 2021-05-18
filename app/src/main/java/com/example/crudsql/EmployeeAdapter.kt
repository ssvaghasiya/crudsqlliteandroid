package com.example.crudsql

import android.app.AlertDialog
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.annotation.Nullable
import java.lang.String


class EmployeeAdapter : ArrayAdapter<Employee> {

    var mCtx: Context? = null
    var listLayoutRes = 0
    var employeeList: ArrayList<Employee>? = null
    var mDatabase: SQLiteDatabase? = null

    constructor(
        mCtx: Context?,
        listLayoutRes: Int,
        employeeList: List<Employee?>?,
        mDatabase: SQLiteDatabase?
    ) : super(mCtx!!, listLayoutRes, employeeList as ArrayList<Employee>) {

        this.mCtx = mCtx
        this.listLayoutRes = listLayoutRes
        this.employeeList = employeeList
        this.mDatabase = mDatabase
    }

    override fun getView(position: Int, @Nullable convertView: View?, parent: ViewGroup): View {
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(listLayoutRes, null)

        //getting employee of the specified position
        val employee = employeeList!![position]


        //getting views
        val textViewName: TextView = view.findViewById(R.id.textViewName)
        val textViewDept: TextView = view.findViewById(R.id.textViewDepartment)
        val textViewSalary: TextView = view.findViewById(R.id.textViewSalary)
        val textViewJoiningDate: TextView = view.findViewById(R.id.textViewJoiningDate)

        //adding data to views
        textViewName.text = employee.name
        textViewDept.text = employee.dept
        textViewSalary.text = String.valueOf(employee.salary)
        textViewJoiningDate.text = employee.joiningDate

        //we will use these buttons later for update and delete operation
        val buttonDelete: Button = view.findViewById(R.id.buttonDeleteEmployee)
        val buttonEdit: Button = view.findViewById(R.id.buttonEditEmployee)

        //adding a clicklistener to button
        buttonEdit.setOnClickListener { updateEmployee(employee) }

        //the delete operation
        buttonDelete.setOnClickListener {
            val builder = AlertDialog.Builder(mCtx)
            builder.setTitle("Are you sure?")
            builder.setPositiveButton(
                "Yes"
            ) { dialogInterface, i ->
                val sql = "DELETE FROM employees WHERE id = ?"
                mDatabase!!.execSQL(sql, arrayOf(employee.id))
                reloadEmployeesFromDatabase()
            }
            builder.setNegativeButton(
                "Cancel"
            ) { dialogInterface, i -> }
            val dialog = builder.create()
            dialog.show()
        }
        return view
    }

    private fun updateEmployee(employee: Employee) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(mCtx)
        val inflater = LayoutInflater.from(mCtx)
        val view: View = inflater.inflate(R.layout.dialog_update_employee, null)
        builder.setView(view)
        val editTextName = view.findViewById<EditText>(R.id.editTextName)
        val editTextSalary = view.findViewById<EditText>(R.id.editTextSalary)
        val spinnerDepartment = view.findViewById<Spinner>(R.id.spinnerDepartment)
        editTextName.setText(employee.name)
        editTextSalary.setText(String.valueOf(employee.salary))
        val dialog: AlertDialog = builder.create()
        dialog.show()
        view.findViewById<View>(R.id.buttonUpdateEmployee)
            .setOnClickListener(View.OnClickListener {
                val name = editTextName.text.toString().trim { it <= ' ' }
                val salary =
                    editTextSalary.text.toString().trim { it <= ' ' }
                val dept = spinnerDepartment.selectedItem.toString()
                if (name.isEmpty()) {
                    editTextName.error = "Name can't be blank"
                    editTextName.requestFocus()
                    return@OnClickListener
                }
                if (salary.isEmpty()) {
                    editTextSalary.error = "Salary can't be blank"
                    editTextSalary.requestFocus()
                    return@OnClickListener
                }
                val sql = """
                    UPDATE employees 
                    SET name = ?, 
                    department = ?, 
                    salary = ? 
                    WHERE id = ?;
                    
                    """.trimIndent()
                mDatabase!!.execSQL(
                    sql,
                    arrayOf(
                        name,
                        dept,
                        salary,
                        String.valueOf(employee.id)
                    )
                )
                Toast.makeText(mCtx, "Employee Updated", Toast.LENGTH_SHORT).show()
                reloadEmployeesFromDatabase()
                dialog.dismiss()
            })
    }

    private fun reloadEmployeesFromDatabase() {
        val cursorEmployees: Cursor = mDatabase!!.rawQuery("SELECT * FROM employees", null)
        if (cursorEmployees.moveToFirst()) {
            employeeList!!.clear()
            do {
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
        cursorEmployees.close()
        notifyDataSetChanged()
    }
}