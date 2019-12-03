INSERT INTO role(name, status, performed_by)
VALUES ('System Admin', 'CREATED', -1);

-- Mapping User CRUD claims to System Admin role
INSERT INTO role_claim(fk_role_id, fk_claim_id, performed_by)
SELECT r.id, c.id, -1
FROM role r,
     claim c
WHERE r.name = 'System Admin'
  AND c.name IN ('UserGetAll', 'UserCreate', 'UserUpdate', 'UserGetById', 'UserDelete');