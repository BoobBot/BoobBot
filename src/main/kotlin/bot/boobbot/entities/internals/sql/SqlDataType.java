package bot.boobbot.entities.internals.sql;

enum SqlDataType {
    /** Signed integer with a value of -128 to 127 */
    TINYINT,
    /** Effectively an alias for TINYINT but with values of 0 and 1 */
    BOOLEAN,
    /** Signed integer with a value of -32,768, 32,767 */
    SMALLINT,
    /** Signed integer with a value of -8,388,608 to 8,388,607 */
    MEDIUMINT,
    /** Signed integer with a value of -2,147,483,648 to 2,147,483,647 */
    INT,
    /** Signed integer with a value of -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807 */
    BIGINT,
    /** A packed "exact" fixed-point number */
    DECIMAL,
    /** Single-precision floating-point number */
    FLOAT,
    /** Normal-size (double-precision) floating-point number */
    DOUBLE,
    BIT,
    /** Binary large object up to 65,535 bytes */
    BLOB,
    /** String with up to 65,535 characters */
    TEXT,
    /** String with up to 4,294,967,295 characters */
    LONGTEXT,
    /** Raw JSON string */
    JSON,
    /**
     * String with variable length up to 65,535 characters.
     * When creating columns, can be combined with (<number>) to denote a maximum size.
     * Always prefer this over TEXT/LONGTEXT where possible as it's more efficient.
     */
    VARCHAR
}
