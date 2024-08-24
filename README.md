WITH UTI_Ranked AS (
    SELECT 
        Deal,
        TransactionId,
        UTI,
        ROW_NUMBER() OVER (PARTITION BY Deal, TransactionId ORDER BY UTI) AS rn
    FROM 
        DCSDB.TransactionTable_AUD
)
SELECT 
    t1.Deal,
    t1.TransactionId,
    t1.UTI AS UTI1,
    t2.UTI AS UTI2
FROM 
    UTI_Ranked t1
JOIN 
    UTI_Ranked t2
ON 
    t1.Deal = t2.Deal
    AND t1.TransactionId = t2.TransactionId
    AND t1.rn = 1
    AND t2.rn = 2
WHERE 
    EXISTS (
        SELECT 1 
        FROM UTI_Ranked t3 
        WHERE t3.Deal = t1.Deal 
          AND t3.TransactionId = t1.TransactionId 
          AND t3.rn = 2
    )
ORDER BY 
    t1.Deal, 
    t1.TransactionId;
