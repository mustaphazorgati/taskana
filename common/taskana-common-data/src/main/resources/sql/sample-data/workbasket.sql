 -- KSC workbaskets
-- WORKBASKET TABLE           (ID                                        , KEY          , CREATED              , MODIFIED             , NAME                       , DOMAIN    , TYPE      , DESCRIPTION                  , OWNER        , CUSTOM_1 , CUSTOM_2  , CUSTOM_3 , CUSTOM_4  , ORG_LEVEL_1   , ORG_LEVEL_2, ORG_LEVEL_3, ORG_LEVEL_4, MARKED_FOR_DELETION  );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000001', 'GPK_KSC'    , '2018-02-01 12:00:00', '2018-02-01 12:00:00', 'Gruppenpostkorb KSC'      , 'DOMAIN_A', 'GROUP'   , 'Gruppenpostkorb KSC'        , 'teamlead-1'   , 'ABCQVW' , ''        , 'xyz4'   , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000002', 'GPK_KSC_1'  , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Gruppenpostkorb KSC 1'    , 'DOMAIN_A', 'GROUP'   , 'Gruppenpostkorb KSC 1'      , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000003', 'GPK_KSC_2'  , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Gruppenpostkorb KSC 2'    , 'DOMAIN_A', 'GROUP'   , 'Gruppenpostkorb KSC 2'      , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000004', 'TEAMLEAD-1' , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK Teamlead KSC 1'       , 'DOMAIN_A', 'PERSONAL', 'PPK Teamlead KSC 1'         , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000005', 'TEAMLEAD-2' , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK Teamlead KSC 2'       , 'DOMAIN_A', 'PERSONAL', 'PPK Teamlead KSC 2'         , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000006', 'USER-1-1'   , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK User 1 KSC 1'         , 'DOMAIN_A', 'PERSONAL', 'PPK User 1 KSC 1'           , ''           , ''       , ''        , ''       , 'custom4z', ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000007', 'USER-1-2'   , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK User 2 KSC 1'         , 'DOMAIN_A', 'PERSONAL', 'PPK User 2 KSC 1'           , 'user-1-2', 'custom1', 'custom2' , 'custom3', 'custom4' , 'versicherung', 'abteilung', 'projekt'  , 'team'     , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000008', 'USER-2-1'   , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK User 1 KSC 2'         , 'DOMAIN_A', 'PERSONAL', 'PPK User 1 KSC 2'           , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000009', 'USER-2-2'   , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK User 2 KSC 2'         , 'DOMAIN_A', 'PERSONAL', 'PPK User 2 KSC 2'           , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000010', 'TPK_VIP'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Themenpostkorb VIP'       , 'DOMAIN_A', 'TOPIC'   , 'Themenpostkorb VIP'         , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000016', 'TPK_VIP_2'  , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Themenpostkorb VIP 2'     , 'DOMAIN_A', 'TOPIC'   , 'Themenpostkorb VIP'         , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );

-- KSC workbaskets Domain_B
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000011', 'GPK_B_KSC'  , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Gruppenpostkorb KSC B'    , 'DOMAIN_B', 'GROUP'   , 'Gruppenpostkorb KSC'        , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000012', 'GPK_B_KSC_1', RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Gruppenpostkorb KSC B1'   , 'DOMAIN_B', 'GROUP'   , 'Gruppenpostkorb KSC 1'      , ''           , 'custom1', 'custom2' , 'custom3', 'custom4' , 'orgl1'       , 'orgl2'    , 'orgl3'    , 'aorgl4'   , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000013', 'GPK_B_KSC_2', RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Gruppenpostkorb KSC B2'   , 'DOMAIN_B', 'GROUP'   , 'Gruppenpostkorb KSC 2'      , ''           , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000014', 'USER-B-1'   , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK User 1 KSC 1 Domain B', 'DOMAIN_B', 'PERSONAL', 'PPK User 1 KSC 1 Domain B'  , ''           , ''       , 'custom20', ''       , 'custom4' , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:100000000000000000000000000000000015', 'USER-B-2'   , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'PPK User 2 KSC 1 Domain B', 'DOMAIN_B', 'PERSONAL', 'PPK User 1 KSC 1 Domain B'  , 'user-1-2'   , 'ABCABC' , 'cust2'   , 'cust3'  , 'cust4'   , 'orgl1'       , 'orgl2'    , 'orgl3'    , 'orgl4'    , false                );

-- Workbaskets for sorting test
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000900', 'sort001'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'basxet0'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000901', 'Sort002'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'Basxet1'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000902', 'sOrt003'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'bAsxet2'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000903', 'soRt004'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'baSxet3'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000904', 'sorT005'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'basXet4'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000905', 'Sort006'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'basxEt5'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000906', 'SOrt007'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'basxeT6'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000907', 'SoRt008'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'BAsxet7'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000908', 'SorT009'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'BaSxet8'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
INSERT INTO WORKBASKET VALUES ('WBI:000000000000000000000000000000000909', 'Sort010'    , RELATIVE_DATE(0)     , RELATIVE_DATE(0)     , 'BasXet9'                  , 'DOMAIN_A'  , 'TOPIC'   , 'Lorem ipsum dolor sit amet.', 'user-1-3'        , ''       , ''        , ''       , ''        , ''            , ''         , ''         , ''         , false                );
