DEFINE APP_SCHEMA=&&USER_NAME

DECLARE
   V_ACL_NAME   VARCHAR2 (100) := 'APEX_ACL-&&APP_SCHEMA..xml';
BEGIN
   BEGIN
      DBMS_NETWORK_ACL_ADMIN.DROP_ACL (ACL => V_ACL_NAME);
   EXCEPTION
      WHEN OTHERS
      THEN
         NULL;                                       -- ACL does not exist yet
   END;

   -- Privilege to connect to a host
   DBMS_NETWORK_ACL_ADMIN.CREATE_ACL (
      ACL           => V_ACL_NAME,
      DESCRIPTION   => 'Accessing the local host',
      PRINCIPAL     => UPPER ('&&APP_SCHEMA'),          -- DB Schema (grantee)
      IS_GRANT      => TRUE,
      PRIVILEGE     => 'connect',
      START_DATE    => NULL,
      END_DATE      => NULL);

   -- Privilege to resolve a hostname (DNS lookup)
   DBMS_NETWORK_ACL_ADMIN.ADD_PRIVILEGE (
      ACL          => V_ACL_NAME,
      PRINCIPAL    => UPPER ('&&APP_SCHEMA'),           -- DB Schema (grantee)
      IS_GRANT     => TRUE,
      PRIVILEGE    => 'resolve',
      START_DATE   => NULL,
      END_DATE     => NULL);

   -- Privilege to resolve a hostname (DNS lookup)
   DBMS_NETWORK_ACL_ADMIN.ADD_PRIVILEGE (
      ACL          => V_ACL_NAME,
      PRINCIPAL    => UPPER ('&&APP_SCHEMA'),           -- DB Schema (grantee)
      IS_GRANT     => TRUE,
      PRIVILEGE    => 'http',
      START_DATE   => NULL,
      END_DATE     => NULL);
      
   -- Privilege to connect to localhost
   DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL (ACL          => V_ACL_NAME,
                                      HOST         => '*',
                                      LOWER_PORT   => 80,
                                      UPPER_PORT   => 9999);

   DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL (ACL          => V_ACL_NAME,
                                      HOST         => '127.0.0.1',
                                      LOWER_PORT   => 80,
                                      UPPER_PORT   => 9999);

   -- Privilege to connect to localhost
   DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL (ACL          => V_ACL_NAME,
                                      HOST         => 'localhost',
                                      LOWER_PORT   => 80,
                                      UPPER_PORT   => 9999);
END;
/

COMMIT
/