
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
