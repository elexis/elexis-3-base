# Mythic 22 Geräteanbindung 
## Description
This plugin connects a Mythic 22 Hematology Analyzer (Orphée) with Elexis using the Ethernet NIC.

## Technical note

### Mapping
Example of a mapping-sample.txt:
```Bash
Kuerzel;LaborItemId
WBC;5d881f512cb41a873bc204
RBC;44a05c4f47a60961310
HGB;425e5408fffbe9092e98
HCT;4dab58ab12e1c7ee2b812
PLT;156b8379f00cdca02b760
LYM;
MON;
NEU;
LYM%;5f5618644d2b866635f10
MON%;ecc6b8c76724051231d14
NEU%;15f4fc5ba251d7c0a130
MCV;e0906cc6504df33d3d816
MCH;e903f9c1446eacbc39948
MCHC;qf7b019ae9e3c91e101850
RDW;
MPV;
EOS;
BAS;
EOS%;4b83ea9387bac26916740
BAS%;e26d20a40b15b2b722e45
```
### Output
Example of output file:
```Bash
MYTHIC 1;;RESULTDATE;16/05/2011TIME;09:44:03MODE;NORMALUNIT;1SEQ;6;0SID;00904PID;ID;NAMETYPE;STANDARDTEST;DIFOPERATOR;PREL;CTCYCLE;NWBC;7.0  ;;;2.0  ;4.0  ;12.0 ;15.0 RBC;4.28 ;;;2.50 ;4.00 ;6.20 ;7.00 HGB;13.5;;;8.5 ;11.0;17.0;19.0HCT;41.4;;;25.0;35.0;55.0;60.0PLT;261  ;;;70   ;150  ;400  ;500  LYM;1.0  ;;;0.7  ;1.0  ;5.0  ;5.5  MON;0.4  ;;;0.0  ;0.1  ;1.0  ;1.1  NEU;5.3  ;;;1.5  ;2.0  ;8.0  ;9.0  LYM%;14.5;;L;15.0;25.0;50.0;55.0MON%;6.1 ;;;1.0 ;2.0 ;10.0;12.0NEU%;75.3;;;45.0;50.0;80.0;85.0MCV;96.7 ;;;70.0 ;80.0 ;100.0;120.0MCH;31.5 ;;;25.0 ;26.0 ;34.0 ;35.0 MCHC;32.6 ;;;28.0 ;31.0 ;35.5 ;37.0 RDW;15.3 ;;;7.0  ;10.0 ;16.0 ;25.0 MPV;8.4  ;;;6.0  ;7.0  ;11.0 ;12.5 EOS;0.2  ;;;0.0  ;0.0  ;0.4  ;0.6  BAS;0.0  ;;;0.0  ;0.0  ;0.2  ;0.3  EOS%;3.4 ;;;0.0 ;0.0 ;5.0 ;8.0 BAS%;0.7 ;;;0.0 ;0.0 ;2.0 ;5.0 WBC CURVE;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;2;3;5;6;7;6;5;4;5;5;5;5;6;8;8;7;6;6;8;11;14;18;26;34;39;44;48;53;62;70;73;72;71;72;72;71;70;66;58;51;47;46;46;45;45;47;49;54;61;71;80;90;101;110;117;128;142;156;171;184;193;198;205;214;224;232;233;231;227;222;214;208;206;202;193;178;166;156;145;133;125;118;107;95;85;78;71;63;55;47;39;32;29;27;23;17;13;11;10;10;9;7;5;4;3;3;3;WBC THRESHOLDS;32;49;0;RBC CURVE;0;0;0;0;0;0;0;0;0;0;0;0;0;1;1;1;1;1;1;1;1;1;2;3;4;5;6;7;8;10;12;17;23;31;39;50;64;81;100;121;146;171;191;208;224;236;243;243;239;234;227;213;195;176;158;139;123;110;99;87;74;62;52;43;36;31;25;20;15;12;9;7;6;5;4;3;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;2;1;1;1;1;1;1;1;1;1;1;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;RBC THRESHOLDS;32;57PLT CURVE;0;0;0;0;0;0;2;5;9;16;24;33;43;52;61;69;75;81;85;88;91;92;91;90;89;86;84;81;78;74;70;66;62;57;53;49;45;42;38;35;33;30;28;26;25;23;21;20;18;17;16;15;14;13;12;11;11;10;9;8;8;7;6;6;5;5;4;4;3;3;3;3;3;3;3;3;3;3;3;3;2;2;2;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;PLT THRESHOLDS;83ALARMS;INTERPRETIVE_WBC;LYM<;INTERPRETIVE_RBC;INTERPRETIVE_PLT;COMMENT;;LMNE MATRIX;Z78;8;Z2d;20;Z2f;8;Z3f;21;40;Z1e;20;Ze;1;Z1f;2;2;Zd;20;Z41;1;Z21;2;0;80;Z2d;40;Zf;4;Zd;4;Z15;10;Z9;4;0;8;Z1d;40;Z6;1;Z18;40;Z2;6;Zf;4;2;Ze;2;0;40;Zd;1;Zc;10;Zf;80;Z3;5;Zf;80;Z9;20;Z3;10;1;42;Zc;1;0;a2;8;Zc;1;0;2a;4;Zc;20;3;82;b4;Zc;4;a;15;b8;;60;Z7;40;Z3;8;28;89;52;Zb;80;0;70;4c;4;90;Za;8;Z2;6;25;Za;4;80;0;40;26;bd;20;Z9;10;8;40;2d;6e;c8;a0;Z7;20;2;1;1;4;c;5b;f1;90;Z8;8;Z2;8;85;85;7c;Z8;6;0;2;0;82;81;6a;fc;Za;80;a8;80;3c;7b;7f;20;Za;a7;28;c;5d;da;Z9;1;0;3;57;a6;a6;fa;Z7;1;Z3;20;3;;3;3f;b8;8;Za;c;43;a7;fa;fc;90;20;Z5;1;Z2;24;7;c2;66;df;f9;e0;Z6;2;Z3;3a;49;d3;97;fc;Z7;18;Z2;10;10;4a;fa;U1;72;80;Z6;a;Z2;80;e4;61;f7;U1;e4;90;Z9;4;17;c3;57;U1;b4;Z8;10;0;20;d6;bd;af;ef;fa;40;Z6;21;Z2;b0;51;cf;4b;ef;eb;80;Z9;d;8b;d7;fb;U1;f0;;Za;8a;20;b9;a9;U1;78;40;Z7;8;0;d;93;ca;ef;U1;c9;Z7;6;40;0;9;79;65;f7;fc;f8;Z8;40;2;24;49;e3;U2;Z9;8;1;c;5a;1f;dd;fd;c8;Z7;2;0;2;6;41;48;4d;be;86;Z7;20;82;1;2;34;U1;37;fe;20;Z7;1;50;0;22;13;a8;3f;a5;40;Z7;2;80;0;49;ce;e6;f5;f4;24;80;Z6;80;Z2;a5;;ef;fb;ef;18;Z8;a;Z2;2;69;f9;7e;c0;Z8;2;80;0;6;ae;3;f6;88;Z8;a;20;0;16;64;52;f7;c6;40;Z7;1;0;2;81;4e;d5;cb;90;Z9;f8;1;14;22;f;8c;b0;Z8;2;0;1;11;30;41;3d;48;Z8;9;Z2;1a;39;20;84;Z2;40;Z6;84;10;0;5;a1;8;91;Z2;8;Z6;2;Z2;3;40;2;41;Zc;c;68;2;40;Z9;8;;Z2;8;2c;80;Zd;45;0;8;c0;Ze;20;40;0;1;Z6;1;Z3;2;21;80;0;4;4;Za;2;c0;20;0;6;90;Z8;40;Z2;2;4;0;20;90;40;Z7;20;0;8;0;1;50;13;16;Z8;20;Z3;10;c0;8;1c;Za;8;40;10;0;1;cd;40;Z6;20;8;Z2;8;c0;80;5;1c;10;Z6;1;Z2;2;30;80;a0;2;58;Z7;8;Z3;13;0;9;d4;80;Za;1;;a;41;40;33;40;Z7;80;Z3;ca;9;62;2c;20;Z8;2;20;42;b4;40;0;79;Z9;81;10;75;33;40;1;14;e0;Z7;50;0;6;b;f6;0;59;4;Z9;2;33;39;7f;32;Za;8;2;48;9f;e0;Z3;40;Z7;a0;4;b;U1;f8;Z2;84;Z8;1;0;a3;dd;64;10;20;Za;1;db;74;88;Zd;4c;e3;80;Zd;1a;e9;a0;Zd;4;a5;Zc;4;;0;1;38;Zc;8;42;1;20;Zb;1;60;40;80;80;Zc;21;4;0;28;Zc;20;82;Ze;60;Z10;8;Z3c;TLMNE SHADE MATRIX;Z78;8;Z2d;20;Z2f;8;Z3f;21;40;Z1e;20;Ze;1;Z1f;2;Ze;20;Z41;1;Z21;2;0;80;Z2d;40;Zf;4;Zd;4;Z15;10;Z9;4;0;8;Z1d;40;Z6;1;Z18;40;Z2;6;Zf;4;2;Ze;2;0;40;Zd;1;Zc;10;Zf;80;Z3;5;Zf;80;Z9;20;Z3;10;1;42;Zc;1;0;a2;Zd;1;0;2a;4;Zc;20;3;82;b4;Zc;4;a;5;88;60;Z7;40;;Z3;8;28;89;52;Zb;80;0;70;4c;4;90;Za;8;Z2;2;5;Za;4;80;0;40;6;3d;20;Z9;10;0;40;2d;26;0;a0;Z7;20;2;1;1;0;c;50;d1;10;Z8;8;Z2;8;85;81;7c;Z8;6;0;2;0;82;80;6a;8c;Za;80;a8;0;2c;4b;43;20;Za;a7;28;c;c;8;Z9;1;0;3;57;86;84;32;Z7;1;Z3;20;3;1;a;28;8;Za;8;;43;a6;40;2c;90;20;Z8;24;7;42;64;8b;21;e0;Z6;2;Z3;3a;49;c1;7;2c;Z7;18;Z2;10;10;4a;92;20;60;80;Z6;a;Z2;80;e0;60;31;2;0;90;Z9;4;3;c2;41;4c;24;Z8;10;0;20;d2;bc;28;4;78;40;Z6;21;Z2;a0;11;c9;41;80;63;80;Z9;8;1;5;60;80;60;Za;88;20;31;80;e8;50;40;Z7;8;;0;d;12;c0;e0;50;89;Z7;6;40;Z2;59;64;50;0;58;Z8;40;2;24;9;63;4;43;Z9;8;1;c;a;1c;c0;21;88;Z7;2;0;2;6;1;48;40;8;6;Z7;20;82;1;2;24;61;31;80;20;Z7;1;10;0;20;13;a8;2c;0;40;Z7;2;Z2;49;4e;22;c4;f0;4;80;Z6;80;Z2;a5;e8;78;8a;18;Z8;a;Z2;2;49;49;3a;80;Z8;2;;80;0;2;8e;2;f2;88;Z8;a;20;0;16;64;12;f2;46;40;Z7;1;0;2;81;4e;c1;4b;80;Z9;78;0;14;22;7;88;b0;Z8;2;0;1;11;20;41;d;48;Z8;9;Z2;1a;39;20;84;Z2;40;Z6;84;10;0;4;21;8;91;Z2;8;Z6;2;Z2;3;40;0;41;Zc;c;68;2;40;Z9;8;Z2;8;2c;80;Zd;45;0;8;c0;Ze;20;40;0;1;Z6;1;;Z3;2;21;80;0;4;4;Za;2;c0;20;0;6;90;Z8;40;Z2;2;4;0;20;90;40;Z9;8;0;1;50;13;16;Z8;20;Z4;40;8;1c;Zb;40;Z2;1;c1;40;Z6;20;8;Z2;8;80;80;1;4;10;Z6;1;Z2;2;30;80;a0;0;48;Z7;8;Z3;13;0;9;84;80;Za;1;a;41;40;21;40;Z7;80;Z3;88;9;62;2c;20;Z8;2;20;42;94;40;;0;79;Z9;81;10;74;12;40;1;4;e0;Z7;50;0;6;1;94;0;51;4;Z9;2;12;20;6c;32;Za;8;2;48;87;20;Z3;40;Z7;a0;4;b;5a;48;Z2;84;Z8;1;0;a3;10;4;10;20;Za;1;ca;50;8;Zd;44;a3;80;Zd;1a;e1;20;Zd;4;a5;Zc;4;0;1;38;Zc;8;42;1;20;Zb;1;40;40;80;80;Zc;21;4;0;28;Zc;20;82;;Ze;60;Z10;8;Z3c;TTHRES 5D LMNE MATRIX;N1X;20;N1Y;15;N2X;35;N2Y;23;EosY;100;LneX;55;LneY;60;LnlX;50;LnlY;28;RneY;105;NmY;46;LmX;69;LmnX;63;LmnY;26;ICX;105;BasoX1;60;BasoY1;26;BasoX2;64;BasoY2;30;BasoX3;70;BasoY3;29;BasoX4;65;BasoY4;20;NHH;2;NHL;2;RLL;2;RLR;3;NLH;2;NLL;2;HLH;2;HLL;2;END_RESULT;16314
```

## How to install
The plugin is implicitly installed by installing the superordinated [feature](https://github.com/elexis/elexis-3-base/tree/master/features/at.gruber.elexis.mythic22.feature). Elexis 2.1.5 or newer is required

## Configuration
The plugin is configured by a mapping file. Use only pair of values that have a corresponding _Laboritem_ in Elexis. Otherwise an Exception will be thrown. 

## License
Copyright (c) 2011, Christian Gruber and MEDEVIT OG
All rights reserved.
