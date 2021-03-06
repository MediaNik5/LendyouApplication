package org.medianik.lendyou.util.sql

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import org.medianik.lendyou.model.bank.Account
import org.medianik.lendyou.model.bank.Payment
import org.medianik.lendyou.model.debt.Debt
import org.medianik.lendyou.model.debt.DebtId
import org.medianik.lendyou.model.debt.DebtInfo
import org.medianik.lendyou.model.person.Passport
import org.medianik.lendyou.model.person.Person
import org.medianik.lendyou.model.person.PersonId
import java.math.BigDecimal
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

class LendyouDatabase(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        const val DATABASE_VERSION = 3
        const val DATABASE_NAME = "Lendyou.db"
        private const val SQL_CREATE_DEBT_ENTRIES = "create table " + DebtEntry.TABLE_NAME + " (" +
                DebtEntry.COLUMN_ID + " INTEGER PRIMARY KEY, " +
                DebtEntry.COLUMN_SUM + " TEXT, " +
                DebtEntry.COLUMN_LENDER + " TEXT, " +
                DebtEntry.COLUMN_DEBTOR + " TEXT, " +
                DebtEntry.COLUMN_DATE_TIME + " INTEGER, " +
                DebtEntry.COLUMN_PAY_PERIOD + " INTEGER, " +
                DebtEntry.COLUMN_FROM + " TEXT, " +
                DebtEntry.COLUMN_TO + " TEXT)"
        private const val SQL_CONTAINS_DEBT =
            "SELECT COUNT(*) FROM " + DebtEntry.TABLE_NAME + " where id = ?"
        private const val SQL_DELETE_DEBT_ENTRIES = "DROP TABLE IF EXISTS " + DebtEntry.TABLE_NAME
        private const val SQL_GET_ALL_DEBTS = "select * from " + DebtEntry.TABLE_NAME

        private const val SQL_CREATE_DEBT_INFO_ENTRIES =
            "create table " + DebtInfoEntry.TABLE_NAME + " (" +
                    DebtInfoEntry.COLUMN_SUM + " TEXT, " +
                    DebtInfoEntry.COLUMN_LENDER + " TEXT, " +
                    DebtInfoEntry.COLUMN_DEBTOR + " TEXT, " +
                    DebtInfoEntry.COLUMN_DATE_TIME + " INTEGER, " +
                    DebtInfoEntry.COLUMN_PAY_PERIOD + " INTEGER)"
        private const val SQL_DELETE_DEBT_INFO_ENTRIES =
            "DROP TABLE IF EXISTS " + DebtInfoEntry.TABLE_NAME
        private const val SQL_GET_ALL_DEBT_INFO = "select * from " + DebtInfoEntry.TABLE_NAME
        private const val SQL_DELETE_DEBT_INFO_WHERE =
            DebtInfoEntry.COLUMN_SUM + " = ? AND " +
                    DebtInfoEntry.COLUMN_LENDER + " = ? AND " +
                    DebtInfoEntry.COLUMN_DEBTOR + " = ? AND " +
                    DebtInfoEntry.COLUMN_DATE_TIME + " = ?"

        private const val SQL_CREATE_PAYMENT_ENTRIES =
            "create table " + PaymentEntry.TABLE_NAME + " (" +
                    PaymentEntry.COLUMN_DATE_TIME + " INTEGER, " +
                    PaymentEntry.COLUMN_SUM + " TEXT, " +
                    PaymentEntry.COLUMN_DEBT_ID + " INTEGER)"
        private const val SQL_DELETE_PAYMENT_ENTRIES =
            "DROP TABLE IF EXISTS " + PaymentEntry.TABLE_NAME
        private const val SQL_GET_ALL_PAYMENTS =
            "select * from " + PaymentEntry.TABLE_NAME + " where " + PaymentEntry.COLUMN_DEBT_ID + " = ?"

        private const val SQL_CREATE_PERSON_ENTRIES =
            "create table " + PersonEntry.TABLE_NAME + " (" +
                    PersonEntry.COLUMN_ID + " TEXT, " +
                    PersonEntry.COLUMN_EMAIL + " TEXT, " +
                    PersonEntry.COLUMN_NAME + " TEXT)"
        private const val SQL_DELETE_PERSON_ENTRIES =
            "DROP TABLE IF EXISTS " + PersonEntry.TABLE_NAME
        private const val SQL_GET_ALL_PERSONS = "select * from " + PersonEntry.TABLE_NAME

        private const val SQL_CREATE_PASSPORT_ENTRIES =
            "create table " + PassportEntry.TABLE_NAME + " (" +
                    PassportEntry.COLUMN_PERSON_ID + " INTEGER, " +
                    PassportEntry.COLUMN_PASSPORT_ID + " TEXT, " +
                    PassportEntry.COLUMN_FIRST_NAME + " TEXT, " +
                    PassportEntry.COLUMN_MIDDLE_NAME + " TEXT, " +
                    PassportEntry.COLUMN_LAST_NAME + " TEXT)"
        private const val SQL_DELETE_PASSPORT_ENTRIES =
            "DROP TABLE IF EXISTS " + PassportEntry.TABLE_NAME
        private const val SQL_GET_PASSPORT_BY_PERSON_ID =
            "select * from " + PassportEntry.TABLE_NAME + " where " + PassportEntry.COLUMN_PERSON_ID + " = ?"


        private val EMPTY_ARGS = arrayOf<String>()
    }

    private val database: SQLiteDatabase = writableDatabase

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_DEBT_ENTRIES)
        db.execSQL(SQL_CREATE_PAYMENT_ENTRIES)
        db.execSQL(SQL_CREATE_PERSON_ENTRIES)
        db.execSQL(SQL_CREATE_PASSPORT_ENTRIES)
        db.execSQL(SQL_CREATE_DEBT_INFO_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_DEBT_ENTRIES)
        db.execSQL(SQL_DELETE_PAYMENT_ENTRIES)
        db.execSQL(SQL_DELETE_PERSON_ENTRIES)
        db.execSQL(SQL_DELETE_PASSPORT_ENTRIES)
        db.execSQL(SQL_DELETE_DEBT_INFO_ENTRIES)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun allPersons(): List<Person> {
        val cursor = database.rawQuery(SQL_GET_ALL_PERSONS, EMPTY_ARGS)
        cursor.use {
            val persons = ArrayList<Person>(cursor.count)
            cursor.move(-1)
            while (cursor.moveToNext()) {
                persons.add(personFromRow(cursor))
            }
            return persons
        }
    }

    private fun personFromRow(cursor: Cursor): Person {
        val id = PersonId(cursor.getString(0))
        return Person(
            id,
            cursor.getString(2),
            cursor.getString(1),
            getPassport(id)
        )
    }

    private fun getPassport(id: PersonId): Passport {
        val cursor = database.rawQuery(SQL_GET_PASSPORT_BY_PERSON_ID, arrayOf(id.value))
        cursor.use {
            if (!cursor.moveToNext())
                throw IllegalArgumentException("Could not find passport for person with id $id")
            return Passport(
                cursor.getString(1),
                cursor.getString(2),
                cursor.getString(3),
                cursor.getString(4),
            )
        }
    }

    fun addPerson(person: Person): Boolean {
        val values = ContentValues(3)

        values.put(PersonEntry.COLUMN_ID, person.id.value)
        values.put(PersonEntry.COLUMN_NAME, person.name)
        values.put(PersonEntry.COLUMN_EMAIL, person.email)

        addPassport(person.passport, person.id)

        return 1L == database.insert(PersonEntry.TABLE_NAME, null, values)
    }

    private fun addPassport(passport: Passport, personId: PersonId): Boolean {
        val values = ContentValues(5)

        values.put(PassportEntry.COLUMN_PERSON_ID, personId.value)
        values.put(PassportEntry.COLUMN_PASSPORT_ID, passport.id)
        values.put(PassportEntry.COLUMN_FIRST_NAME, passport.firstName)
        values.put(PassportEntry.COLUMN_MIDDLE_NAME, passport.middleName)
        values.put(PassportEntry.COLUMN_LAST_NAME, passport.lastName)

        return 1L == database.insert(PassportEntry.TABLE_NAME, null, values)
    }

    fun allDebts(): List<Debt> {
        val cursor = database.rawQuery(SQL_GET_ALL_DEBTS, EMPTY_ARGS)
        cursor.use {
            val debts = ArrayList<Debt>(cursor.count)
            while (cursor.moveToNext()) {
                debts.add(debtFromRow(cursor))
            }
            return debts
        }
    }

    private fun debtFromRow(cursor: Cursor) = Debt(
        DebtInfo(
            BigDecimal(cursor.getString(1)),
            PersonId(cursor.getString(2)),
            PersonId(cursor.getString(3)),
            LocalDateTime.ofEpochSecond(cursor.getLong(4), 0, ZoneOffset.UTC),
            Duration.ofDays(cursor.getLong(5))
        ),
        Account(cursor.getString(6)),
        Account(cursor.getString(7)),
        DebtId(cursor.getLong(0)),
        allPayments(DebtId(cursor.getLong(0))),
    )

    fun allPendingDebts(): List<DebtInfo> {
        val cursor = database.rawQuery(SQL_GET_ALL_DEBT_INFO, EMPTY_ARGS)
        cursor.use {
            val debtInfos = ArrayList<DebtInfo>(cursor.count)
            while (cursor.moveToNext()) {
                debtInfos.add(debtInfoFromRow(cursor))
            }
            return debtInfos
        }
    }

    private fun debtInfoFromRow(cursor: Cursor): DebtInfo =
        DebtInfo(
            BigDecimal(cursor.getString(0)),
            PersonId(cursor.getString(1)),
            PersonId(cursor.getString(2)),
            LocalDateTime.ofEpochSecond(cursor.getLong(3), 0, ZoneOffset.UTC),
            Duration.ofDays(cursor.getLong(4)),
        )

    fun allPayments(debtId: DebtId): MutableList<Payment> {
        val cursor = database.rawQuery(SQL_GET_ALL_PAYMENTS, arrayOf(debtId.id.toString()))
        cursor.use {
            val payments = ArrayList<Payment>(cursor.count)
            while (cursor.moveToNext()) {
                payments.add(
                    Payment(
                        LocalDateTime.ofEpochSecond(cursor.getLong(0), 0, ZoneOffset.UTC),
                        BigDecimal(cursor.getString(1)),
                        cursor.getLong(2)
                    )
                )
            }
            return payments
        }
    }

    fun contains(debt: Debt?): Boolean {
        if (debt == null)
            return false

        val cursor = database.rawQuery(SQL_CONTAINS_DEBT, arrayOf(debt.id.id.toString()))
        cursor.use {
            return cursor.count == 1
        }
    }

    fun addDebt(debt: Debt): Boolean {
        val values = ContentValues(7)

        values.put(DebtEntry.COLUMN_ID, debt.id.id)
        values.put(DebtEntry.COLUMN_SUM, debt.debtInfo.sum.toString())
        values.put(DebtEntry.COLUMN_LENDER, debt.debtInfo.lenderId.value)
        values.put(DebtEntry.COLUMN_DEBTOR, debt.debtInfo.debtorId.value)
        values.put(DebtEntry.COLUMN_DATE_TIME, debt.debtInfo.dateTime.toEpochSecond(ZoneOffset.UTC))
        values.put(DebtEntry.COLUMN_PAY_PERIOD, debt.debtInfo.payPeriod.toDays())
        values.put(DebtEntry.COLUMN_FROM, debt.from.toString())
        values.put(DebtEntry.COLUMN_TO, debt.to.toString())

        val debtAdded = 1L == database.insert(DebtEntry.TABLE_NAME, null, values)
        val paymentsAdded = addPayments(debt.getPayments())
        return debtAdded && paymentsAdded
    }

    private fun addPayments(payments: List<Payment>): Boolean {
        if (payments.isEmpty())
            return true
        var added = true
        for (payment in payments) {
            added = added && addPayment(payment)
        }
        return added
    }

    fun addPayment(payment: Payment): Boolean {
        val values = ContentValues(3)

        values.put(PaymentEntry.COLUMN_DATE_TIME, payment.dateTime.toEpochSecond(ZoneOffset.UTC))
        values.put(PaymentEntry.COLUMN_SUM, payment.sum.toString())
        values.put(PaymentEntry.COLUMN_DEBT_ID, payment.debtId)
        return 1L == database.insert(PaymentEntry.TABLE_NAME, null, values)
    }

    fun addPendingDebt(debtInfo: DebtInfo): Boolean {
        val values = ContentValues(4)

        values.put(DebtEntry.COLUMN_SUM, debtInfo.sum.toString())
        values.put(DebtEntry.COLUMN_LENDER, debtInfo.lenderId.value)
        values.put(DebtEntry.COLUMN_DEBTOR, debtInfo.debtorId.value)
        values.put(DebtEntry.COLUMN_DATE_TIME, debtInfo.dateTime.toEpochSecond(ZoneOffset.UTC))
        values.put(DebtEntry.COLUMN_PAY_PERIOD, debtInfo.payPeriod.toDays())

        return database.insert(DebtInfoEntry.TABLE_NAME, null, values) == 1L
    }

    fun removePendingDebt(debtInfo: DebtInfo): Boolean {
        return 1 == database.delete(
            DebtInfoEntry.TABLE_NAME,
            SQL_DELETE_DEBT_INFO_WHERE,
            arrayOf(
                debtInfo.sum.toString(),
                debtInfo.lenderId.value,
                debtInfo.debtorId.value,
                debtInfo.dateTime.toEpochSecond(ZoneOffset.UTC).toString(),
            )
        )
    }

}