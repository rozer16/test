
	•	Date & Time: 3rd Sept 2025
	•	System/Service Impacted: Limit Override functionality & related workflows
	•	Duration of Outage: ~3 hours (12:55 PM – 4:17 PM)
	•	Impact: Users unable to perform withdrawals; 6 workflows corrupted and needed to be recreated

⸻

Timeline
	•	12:55 – Changes deployed
	•	1:40 – User raised issue
	•	2:40 – Investigation started; issue identified in GET API → fix initiated & tested
	•	3:10 – Build prepared to fix in production
	•	3:22 – Fix deployed in production, but unsuccessful
	•	3:47 – Rolled back all module changes in prod & COB
	•	3:51 – All functionalities restored and informed users
	•	4:05 – Users unable to withdraw workflows (corrupted)
	•	4:17 – 6 corrupted workflows withdrawn from backend and new ones successfully created

⸻

Root Cause

DB Stored Procedure was not deployed in production with expected changes, leading to partial deployment failure.

⸻

Contributing Factors
	•	No post-deployment validation checklist in place
	•	Direct DB changes bypassing standard deployment process
	•	No automated verification to confirm stored procedure updates

⸻

Mitigation Steps Taken
	•	Rolled back changes in production & COB
	•	Redeployed workflows and recreated corrupted entries
	•	Restored all services and validated functionality with users

⸻

Business Impact
	•	Downtime: ~3 hours
	•	Users Affected: All users of limit override functionality
	•	Workflows Impacted: 6 corrupted workflows had to be withdrawn & recreated

⸻

Lessons Learned
	•	Importance of validating stored procedure deployments immediately after release
	•	Avoid direct DB modifications outside approved deployment tools
	•	Need for automated DB deployment verification

⸻

Corrective Actions
	1.	Establish a deployment validation checklist (esp. for DB changes).
	2.	Enforce usage of uDeploy only (no direct DB changes).
	3.	Implement a read-view validation mechanism to confirm stored procedure deployment status.
