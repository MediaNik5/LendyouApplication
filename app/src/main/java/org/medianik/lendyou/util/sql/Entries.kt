package org.medianik.lendyou.util.sql

internal object DebtEntry {
    const val TABLE_NAME = "debt"

    const val COLUMN_ID = "id"
    const val COLUMN_SUM = "sum"
    const val COLUMN_LENDER = "lender_id"
    const val COLUMN_DEBTOR = "debtor_id"
    const val COLUMN_DATE_TIME = "date_time"
    const val COLUMN_FROM = "from_account"
    const val COLUMN_TO = "to_account"
    const val COLUMN_PAY_PERIOD = "pay_period"
}

internal object PaymentEntry {
    const val TABLE_NAME = "payment"

    const val COLUMN_ID = "id"
    const val COLUMN_DATE_TIME = "date_time"
    const val COLUMN_SUM = "sum"
    const val COLUMN_FROM = "from_account"
    const val COLUMN_TO = "to_account"
    const val COLUMN_DEBT_ID = "debt_id"
}

internal object PersonEntry {
    const val TABLE_NAME = "lender"

    const val COLUMN_ID = "id"
    const val COLUMN_NAME = "name"
    const val COLUMN_PHONE = "phone"
}

internal object PassportEntry {
    const val TABLE_NAME = "passport"

    const val COLUMN_PERSON_ID = "person_id"
    const val COLUMN_PASSPORT_ID = "passport_id"
    const val COLUMN_FIRST_NAME = "first_name"
    const val COLUMN_MIDDLE_NAME = "middle_name"
    const val COLUMN_LAST_NAME = "last_name"
}