package com.example.crudsql

class Employee {
    var id = 0
    var name: String? = null
    var dept: String? = null
    var joiningDate: String? = null
    var salary = 0.0

    constructor(
        id: Int,
        name: String?,
        dept: String,
        joiningDate: String,
        salary: Double
    ) {
        this.id = id
        this.name = name
        this.dept = dept
        this.joiningDate = joiningDate
        this.salary = salary
    }
}