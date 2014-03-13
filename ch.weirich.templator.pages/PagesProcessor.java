// Compiled from PagesProcessor.java (version 1.6 : 50.0, super bit)
public class ch.weirich.templator.pages.PagesProcessor implements ch.medelexis.templator.model.IProcessor {
  
  // Field descriptor #8 Lch/medelexis/templator/model/ProcessingSchema;
  private ch.medelexis.templator.model.ProcessingSchema proc;
  
  // Method descriptor #10 ()V
  // Stack: 1, Locals: 1
  public PagesProcessor();
    0  aload_0 [this]
    1  invokespecial java.lang.Object() [12]
    4  return
      Line numbers:
        [pc: 0, line: 26]
        [pc: 4, line: 28]
      Local variable table:
        [pc: 0, pc: 5] local: this index: 0 type: ch.weirich.templator.pages.PagesProcessor
  
  // Method descriptor #19 ()Ljava/lang/String;
  // Stack: 1, Locals: 1
  public java.lang.String getName();
    0  ldc <String "Apple(tm) iWork(tm) Pages(tm)"> [20]
    2  areturn
      Line numbers:
        [pc: 0, line: 32]
      Local variable table:
        [pc: 0, pc: 3] local: this index: 0 type: ch.weirich.templator.pages.PagesProcessor
  
  // Method descriptor #23 (Lch/medelexis/templator/model/ProcessingSchema;)Z
  // Stack: 6, Locals: 13
  public boolean doOutput(ch.medelexis.templator.model.ProcessingSchema schema);
      0  aload_0 [this]
      1  aload_1 [schema]
      2  putfield ch.weirich.templator.pages.PagesProcessor.proc : ch.medelexis.templator.model.ProcessingSchema [24]
      5  aload_1 [schema]
      6  invokevirtual ch.medelexis.templator.model.ProcessingSchema.getTemplateFile() : java.io.File [26]
      9  astore_2 [tmpl]
     10  aload_2 [tmpl]
     11  invokevirtual java.io.File.exists() : boolean [32]
     14  ifne 40
     17  ldc <String "Template missing"> [38]
     19  ldc <String "Konnte Vorlagedatei {0} nicht öffnen"> [40]
     21  iconst_1
     22  anewarray java.lang.Object [3]
     25  dup
     26  iconst_0
     27  aload_2 [tmpl]
     28  invokevirtual java.io.File.getAbsolutePath() : java.lang.String [42]
     31  aastore
     32  invokestatic java.text.MessageFormat.format(java.lang.String, java.lang.Object[]) : java.lang.String [45]
     35  invokestatic ch.elexis.util.SWTHelper.alert(java.lang.String, java.lang.String) : void [51]
     38  iconst_0
     39  ireturn
     40  new java.util.zip.ZipInputStream [57]
     43  dup
     44  new java.io.FileInputStream [59]
     47  dup
     48  aload_2 [tmpl]
     49  invokespecial java.io.FileInputStream(java.io.File) [61]
     52  invokespecial java.util.zip.ZipInputStream(java.io.InputStream) [64]
     55  astore_3 [zis]
     56  invokestatic ch.elexis.actions.ElexisEventDispatcher.getSelectedPatient() : ch.elexis.data.Patient [67]
     59  astore 4 [actPatient]
     61  invokestatic ch.medelexis.templator.model.StorageController.getInstance() : ch.medelexis.templator.model.StorageController [73]
     64  astore 5 [sc]
     66  aconst_null
     67  astore 6 [output]
     69  aload 5 [sc]
     71  aload 4 [actPatient]
     73  aload_2 [tmpl]
     74  invokevirtual java.io.File.getName() : java.lang.String [79]
     77  invokevirtual ch.medelexis.templator.model.StorageController.createFile(ch.elexis.data.Patient, java.lang.String) : java.io.File [81]
     80  astore 6 [output]
     82  new java.util.zip.ZipOutputStream [85]
     85  dup
     86  new java.io.FileOutputStream [87]
     89  dup
     90  aload 6 [output]
     92  invokespecial java.io.FileOutputStream(java.io.File) [89]
     95  invokespecial java.util.zip.ZipOutputStream(java.io.OutputStream) [90]
     98  astore 7 [zos]
    100  goto 175
    103  new java.util.zip.ZipEntry [93]
    106  dup
    107  aload 8 [ze]
    109  invokevirtual java.util.zip.ZipEntry.getName() : java.lang.String [95]
    112  invokespecial java.util.zip.ZipEntry(java.lang.String) [96]
    115  astore 9 [zo]
    117  aload 7 [zos]
    119  aload 9 [zo]
    121  invokevirtual java.util.zip.ZipOutputStream.putNextEntry(java.util.zip.ZipEntry) : void [99]
    124  aload 9 [zo]
    126  bipush 8
    128  invokevirtual java.util.zip.ZipEntry.setMethod(int) : void [103]
    131  aload 8 [ze]
    133  invokevirtual java.util.zip.ZipEntry.getName() : java.lang.String [95]
    136  ldc <String "index.xml"> [107]
    138  invokevirtual java.lang.String.equals(java.lang.Object) : boolean [109]
    141  ifeq 169
    144  new ch.medelexis.templator.model.SchemaFilterOutputStream [115]
    147  dup
    148  aload_0 [this]
    149  getfield ch.weirich.templator.pages.PagesProcessor.proc : ch.medelexis.templator.model.ProcessingSchema [24]
    152  aload 7 [zos]
    154  aload_0 [this]
    155  invokespecial ch.medelexis.templator.model.SchemaFilterOutputStream(ch.medelexis.templator.model.ProcessingSchema, java.io.OutputStream, ch.medelexis.templator.model.IProcessor) [117]
    158  astore 10 [sfo]
    160  aload_3 [zis]
    161  aload 10 [sfo]
    163  invokestatic ch.rgw.io.FileTool.copyStreams(java.io.InputStream, java.io.OutputStream) : void [120]
    166  goto 175
    169  aload_3 [zis]
    170  aload 7 [zos]
    172  invokestatic ch.rgw.io.FileTool.copyStreams(java.io.InputStream, java.io.OutputStream) : void [120]
    175  aload_3 [zis]
    176  invokevirtual java.util.zip.ZipInputStream.getNextEntry() : java.util.zip.ZipEntry [126]
    179  dup
    180  astore 8 [ze]
    182  ifnonnull 103
    185  aload 7 [zos]
    187  invokevirtual java.util.zip.ZipOutputStream.finish() : void [130]
    190  aload_3 [zis]
    191  invokevirtual java.util.zip.ZipInputStream.close() : void [133]
    194  aload 7 [zos]
    196  invokevirtual java.util.zip.ZipOutputStream.close() : void [136]
    199  getstatic ch.elexis.Hub.localCfg : ch.rgw.io.Settings [137]
    202  new java.lang.StringBuilder [143]
    205  dup
    206  getstatic ch.weirich.templator.pages.Preferences.PREFERENCE_BRANCH : java.lang.String [145]
    209  invokestatic java.lang.String.valueOf(java.lang.Object) : java.lang.String [151]
    212  invokespecial java.lang.StringBuilder(java.lang.String) [155]
    215  ldc <String "cmd"> [156]
    217  invokevirtual java.lang.StringBuilder.append(java.lang.String) : java.lang.StringBuilder [158]
    220  invokevirtual java.lang.StringBuilder.toString() : java.lang.String [162]
    223  ldc <String "open"> [165]
    225  invokevirtual ch.rgw.io.Settings.get(java.lang.String, java.lang.String) : java.lang.String [167]
    228  astore 9 [cmd]
    230  getstatic ch.elexis.Hub.localCfg : ch.rgw.io.Settings [137]
    233  ldc <String "briefe/medelexis-templator/oooprocessor/param"> [173]
    235  ldc <String "%"> [175]
    237  invokevirtual ch.rgw.io.Settings.get(java.lang.String, java.lang.String) : java.lang.String [167]
    240  astore 10 [param]
    242  aload 10 [param]
    244  bipush 37
    246  invokevirtual java.lang.String.indexOf(int) : int [177]
    249  istore 11 [i]
    251  iload 11 [i]
    253  iconst_m1
    254  if_icmpeq 300
    257  new java.lang.StringBuilder [143]
    260  dup
    261  aload 10 [param]
    263  iconst_0
    264  iload 11 [i]
    266  invokevirtual java.lang.String.substring(int, int) : java.lang.String [181]
    269  invokestatic java.lang.String.valueOf(java.lang.Object) : java.lang.String [151]
    272  invokespecial java.lang.StringBuilder(java.lang.String) [155]
    275  aload 6 [output]
    277  invokevirtual java.io.File.getAbsolutePath() : java.lang.String [42]
    280  invokevirtual java.lang.StringBuilder.append(java.lang.String) : java.lang.StringBuilder [158]
    283  aload 10 [param]
    285  iload 11 [i]
    287  iconst_1
    288  iadd
    289  invokevirtual java.lang.String.substring(int) : java.lang.String [185]
    292  invokevirtual java.lang.StringBuilder.append(java.lang.String) : java.lang.StringBuilder [158]
    295  invokevirtual java.lang.StringBuilder.toString() : java.lang.String [162]
    298  astore 10 [param]
    300  invokestatic java.lang.Runtime.getRuntime() : java.lang.Runtime [188]
    303  iconst_2
    304  anewarray java.lang.String [110]
    307  dup
    308  iconst_0
    309  aload 9 [cmd]
    311  aastore
    312  dup
    313  iconst_1
    314  aload 10 [param]
    316  aastore
    317  invokevirtual java.lang.Runtime.exec(java.lang.String[]) : java.lang.Process [194]
    320  astore 12 [process]
    322  aload 12 [process]
    324  invokevirtual java.lang.Process.waitFor() : int [198]
    327  ifne 332
    330  iconst_1
    331  ireturn
    332  iconst_0
    333  ireturn
    334  astore_3 [e]
    335  aload_3 [e]
    336  invokestatic ch.rgw.tools.ExHandler.handle(java.lang.Throwable) : void [204]
    339  ldc <String "Pages Processor"> [210]
    341  new java.lang.StringBuilder [143]
    344  dup
    345  ldc <String "Problem mit dem Erstellen des Dokuments "> [212]
    347  invokespecial java.lang.StringBuilder(java.lang.String) [155]
    350  aload_3 [e]
    351  invokevirtual java.lang.Exception.getMessage() : java.lang.String [214]
    354  invokevirtual java.lang.StringBuilder.append(java.lang.String) : java.lang.StringBuilder [158]
    357  invokevirtual java.lang.StringBuilder.toString() : java.lang.String [162]
    360  invokestatic ch.elexis.util.SWTHelper.alert(java.lang.String, java.lang.String) : void [51]
    363  iconst_0
    364  ireturn
      Exception Table:
        [pc: 40, pc: 333] -> 334 when : java.lang.Exception
      Line numbers:
        [pc: 0, line: 37]
        [pc: 5, line: 38]
        [pc: 10, line: 39]
        [pc: 17, line: 40]
        [pc: 19, line: 41]
        [pc: 27, line: 42]
        [pc: 32, line: 40]
        [pc: 38, line: 43]
        [pc: 40, line: 46]
        [pc: 56, line: 47]
        [pc: 61, line: 48]
        [pc: 66, line: 49]
        [pc: 69, line: 50]
        [pc: 82, line: 51]
        [pc: 90, line: 52]
        [pc: 95, line: 51]
        [pc: 100, line: 54]
        [pc: 103, line: 55]
        [pc: 117, line: 56]
        [pc: 124, line: 57]
        [pc: 131, line: 58]
        [pc: 144, line: 59]
        [pc: 148, line: 60]
        [pc: 155, line: 59]
        [pc: 160, line: 61]
        [pc: 166, line: 62]
        [pc: 169, line: 63]
        [pc: 175, line: 54]
        [pc: 185, line: 67]
        [pc: 190, line: 68]
        [pc: 194, line: 69]
        [pc: 199, line: 70]
        [pc: 202, line: 71]
        [pc: 225, line: 70]
        [pc: 230, line: 72]
        [pc: 235, line: 73]
        [pc: 237, line: 72]
        [pc: 242, line: 74]
        [pc: 251, line: 75]
        [pc: 257, line: 76]
        [pc: 283, line: 77]
        [pc: 295, line: 76]
        [pc: 300, line: 79]
        [pc: 303, line: 80]
        [pc: 317, line: 79]
        [pc: 322, line: 81]
        [pc: 334, line: 82]
        [pc: 335, line: 83]
        [pc: 339, line: 85]
        [pc: 341, line: 86]
        [pc: 350, line: 87]
        [pc: 357, line: 86]
        [pc: 360, line: 85]
        [pc: 363, line: 89]
      Local variable table:
        [pc: 0, pc: 365] local: this index: 0 type: ch.weirich.templator.pages.PagesProcessor
        [pc: 0, pc: 365] local: schema index: 1 type: ch.medelexis.templator.model.ProcessingSchema
        [pc: 10, pc: 365] local: tmpl index: 2 type: java.io.File
        [pc: 56, pc: 334] local: zis index: 3 type: java.util.zip.ZipInputStream
        [pc: 61, pc: 334] local: actPatient index: 4 type: ch.elexis.data.Patient
        [pc: 66, pc: 334] local: sc index: 5 type: ch.medelexis.templator.model.StorageController
        [pc: 69, pc: 334] local: output index: 6 type: java.io.File
        [pc: 100, pc: 334] local: zos index: 7 type: java.util.zip.ZipOutputStream
        [pc: 103, pc: 175] local: ze index: 8 type: java.util.zip.ZipEntry
        [pc: 182, pc: 334] local: ze index: 8 type: java.util.zip.ZipEntry
        [pc: 117, pc: 175] local: zo index: 9 type: java.util.zip.ZipEntry
        [pc: 160, pc: 166] local: sfo index: 10 type: ch.medelexis.templator.model.SchemaFilterOutputStream
        [pc: 230, pc: 334] local: cmd index: 9 type: java.lang.String
        [pc: 242, pc: 334] local: param index: 10 type: java.lang.String
        [pc: 251, pc: 334] local: i index: 11 type: int
        [pc: 322, pc: 334] local: process index: 12 type: java.lang.Process
        [pc: 335, pc: 363] local: e index: 3 type: java.lang.Exception
      Stack map table: number of frames 7
        [pc: 40, append: {java.io.File}]
        [pc: 103, full, stack: {}, locals: {ch.weirich.templator.pages.PagesProcessor, ch.medelexis.templator.model.ProcessingSchema, java.io.File, java.util.zip.ZipInputStream, ch.elexis.data.Patient, ch.medelexis.templator.model.StorageController, java.io.File, java.util.zip.ZipOutputStream, java.util.zip.ZipEntry}]
        [pc: 169, append: {java.util.zip.ZipEntry}]
        [pc: 175, chop 2 local(s)]
        [pc: 300, full, stack: {}, locals: {ch.weirich.templator.pages.PagesProcessor, ch.medelexis.templator.model.ProcessingSchema, java.io.File, java.util.zip.ZipInputStream, ch.elexis.data.Patient, ch.medelexis.templator.model.StorageController, java.io.File, java.util.zip.ZipOutputStream, java.util.zip.ZipEntry, java.lang.String, java.lang.String, int}]
        [pc: 332, append: {java.lang.Process}]
        [pc: 334, full, stack: {java.lang.Exception}, locals: {ch.weirich.templator.pages.PagesProcessor, ch.medelexis.templator.model.ProcessingSchema, java.io.File}]
  
  // Method descriptor #247 (Ljava/lang/String;)Ljava/lang/String;
  // Stack: 3, Locals: 3
  public java.lang.String convert(java.lang.String input);
     0  aload_1 [input]
     1  ldc <String "\t"> [248]
     3  ldc <String "<sf:tab/>"> [250]
     5  invokevirtual java.lang.String.replaceAll(java.lang.String, java.lang.String) : java.lang.String [252]
     8  astore_2 [replacement]
     9  aload_2 [replacement]
    10  ldc <String "\n"> [255]
    12  ldc_w <String "<sf:br/>"> [257]
    15  invokevirtual java.lang.String.replaceAll(java.lang.String, java.lang.String) : java.lang.String [252]
    18  astore_2 [replacement]
    19  aload_2 [replacement]
    20  areturn
      Line numbers:
        [pc: 0, line: 94]
        [pc: 9, line: 95]
        [pc: 19, line: 96]
      Local variable table:
        [pc: 0, pc: 21] local: this index: 0 type: ch.weirich.templator.pages.PagesProcessor
        [pc: 0, pc: 21] local: input index: 1 type: java.lang.String
        [pc: 9, pc: 21] local: replacement index: 2 type: java.lang.String
}