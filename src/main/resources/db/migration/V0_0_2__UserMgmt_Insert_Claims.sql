INSERT INTO claim(resource_name, resource_http_method, resource_endpoint, status, performed_by) VALUES
('UserGetAll', 'GET', '/api/v1/user', 'CREATED', 1),
('UserCreate', 'POST', '/api/v1/user', 'CREATED', -1),
('UserUpdate', 'PUT', '/api/v1/user', 'CREATED', -1),
('UserGetById', 'GET', '/api/v1/user/{uuid}', 'CREATED', -1),
('UserDelete', 'DELETE', '/api/v1/user/{uuid}', 'CREATED', -1),

('PermissionGetAll', 'GET', '/api/v1/claim', 'CREATED', -1),

('RoleGetAll', 'GET', '/api/v1/role', 'CREATED', -1),
('RoleCreate', 'POST', '/api/v1/role', 'CREATED', -1),
('RoleUpdate', 'PUT', '/api/v1/role', 'CREATED', -1),
('RoleDelete', 'DELETE', '/api/v1/role/{uuid}', 'CREATED', -1);