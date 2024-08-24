WITH UTI_Versions AS (
    SELECT 
        Deal,
        TransactionId,
        UTI,
        lasteconomoicModification,
        ROW_NUMBER() OVER (PARTITION BY Deal, TransactionId ORDER BY lasteconomoicModification ASC) AS rn_min,
        ROW_NUMBER() OVER (PARTITION BY Deal, TransactionId ORDER BY lasteconomoicModification DESC) AS rn_max,
        COUNT(DISTINCT UTI) OVER (PARTITION BY Deal, TransactionId) AS uti_count
    FROM 
        DCSDB.TransactionTable_AUD
)
SELECT 
    min_uti.Deal,
    min_uti.TransactionId,
    min_uti.UTI AS UTI1,
    max_uti.UTI AS UTI2,
    max_uti.lasteconomoicModification
FROM 
    UTI_Versions min_uti
JOIN 
    UTI_Versions max_uti
ON 
    min_uti.Deal = max_uti.Deal
    AND min_uti.TransactionId = max_uti.TransactionId
    AND min_uti.rn_min = 1
    AND max_uti.rn_max = 1
WHERE 
    min_uti.uti_count > 1
ORDER BY 
    min_uti.Deal, 
    min_uti.TransactionId;
