My system is about trade 

I have an table with name : DCSDB.TransactionTable_Aud having rev column with incremental value with each version of new amendment of trade and primarily column or composite key for this table is (Deal+TransactionId)


I also have another column UTI in audit table which is primary key for downstream but for my system primary key is (deal+transactionId)

Through out trade life cycle  UTI once stamped for given composite key(deal+transactionid), it should not be updated but due to error in my code one (deal+transactionId ) have multiple UniqueInternalTradeId in audit table 


here is another column last economic modification which tells when UTI was updated


Can you please give query with all deals having multiple UniqueInternalTradeId with lastEconomicModification
I want to know all composite key(deal+transactionId) having multiple uniqueInternalTradeId with all values


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



    a.Deal,
    



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
ORDER BY 
    t1.Deal, 
    t1.TransactionId;
