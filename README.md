
SELECT 
    Deal,
    TransactionId,
    LISTAGG(UniqueInternalTradeId || ' (LastMod: ' || TO_CHAR(lastEconomicModification, 'YYYY-MM-DD HH24:MI:SS') || ')', ', ') 
        WITHIN GROUP (ORDER BY lastEconomicModification) AS UniqueInternalTradeIds_Modifications,
    COUNT(DISTINCT UniqueInternalTradeId) AS UniqueInternalTradeId_Count
FROM 
    audit_table  -- Replace with your actual table name
GROUP BY 
    Deal,
    TransactionId
HAVING 
    COUNT(DISTINCT UniqueInternalTradeId) > 1
ORDER BY 
    Deal,
    TransactionId;


WITH DuplicateTradeIds AS (
    SELECT 
        Deal,
        TransactionId,
        COUNT(DISTINCT UniqueInternalTradeId) AS UniqueInternalTradeId_Count
    FROM 
        audit_table  -- Replace with your actual table name
    GROUP BY 
        Deal,
        TransactionId
    HAVING 
        COUNT(DISTINCT UniqueInternalTradeId) > 1
)
SELECT 
    a.Deal,
    a.TransactionId,
    a.UniqueInternalTradeId,
    TO_CHAR(a.lastEconomicModification, 'YYYY-MM-DD HH24:MI:SS') AS LastEconomicModification
FROM 
    audit_table a
INNER JOIN 
    DuplicateTradeIds d
ON 
    a.Deal = d.Deal AND 
    a.TransactionId = d.TransactionId
ORDER BY 
    a.Deal,
    a.TransactionId,
    a.lastEconomicModification;