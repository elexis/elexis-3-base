// Compiled from Preferences.java (version 1.6 : 50.0, super bit)
public class ch.weirich.templator.pages.Preferences extends org.eclipse.jface.preference.FieldEditorPreferencePage implements org.eclipse.ui.IWorkbenchPreferencePage {
  
  // Field descriptor #8 Lorg/eclipse/jface/preference/IPreferenceStore;
  org.eclipse.jface.preference.IPreferenceStore store;
  
  // Field descriptor #10 Ljava/lang/String;
  public static final java.lang.String PREFERENCE_BRANCH;
  
  // Method descriptor #12 ()V
  // Stack: 3, Locals: 0
  static {};
     0  new java.lang.StringBuilder [14]
     3  dup
     4  getstatic ch.weirich.templator.pages.Preferences.PREFERENCE_BRANCH : java.lang.String [16]
     7  invokestatic java.lang.String.valueOf(java.lang.Object) : java.lang.String [18]
    10  invokespecial java.lang.StringBuilder(java.lang.String) [24]
    13  ldc <String "pagesprocessor/"> [28]
    15  invokevirtual java.lang.StringBuilder.append(java.lang.String) : java.lang.StringBuilder [30]
    18  invokevirtual java.lang.StringBuilder.toString() : java.lang.String [34]
    21  putstatic ch.weirich.templator.pages.Preferences.PREFERENCE_BRANCH : java.lang.String [16]
    24  return
      Line numbers:
        [pc: 0, line: 19]
        [pc: 13, line: 20]
        [pc: 18, line: 19]
        [pc: 24, line: 20]
  
  // Method descriptor #12 ()V
  // Stack: 4, Locals: 1
  public Preferences();
     0  aload_0 [this]
     1  iconst_1
     2  invokespecial org.eclipse.jface.preference.FieldEditorPreferencePage(int) [40]
     5  aload_0 [this]
     6  new ch.elexis.preferences.SettingsPreferenceStore [43]
     9  dup
    10  getstatic ch.elexis.Hub.localCfg : ch.rgw.io.Settings [45]
    13  invokespecial ch.elexis.preferences.SettingsPreferenceStore(ch.rgw.io.Settings) [51]
    16  putfield ch.weirich.templator.pages.Preferences.store : org.eclipse.jface.preference.IPreferenceStore [54]
    19  aload_0 [this]
    20  aload_0 [this]
    21  getfield ch.weirich.templator.pages.Preferences.store : org.eclipse.jface.preference.IPreferenceStore [54]
    24  invokevirtual ch.weirich.templator.pages.Preferences.setPreferenceStore(org.eclipse.jface.preference.IPreferenceStore) : void [56]
    27  return
      Line numbers:
        [pc: 0, line: 23]
        [pc: 5, line: 24]
        [pc: 19, line: 25]
        [pc: 27, line: 26]
      Local variable table:
        [pc: 0, pc: 28] local: this index: 0 type: ch.weirich.templator.pages.Preferences
  
  // Method descriptor #63 (Lorg/eclipse/ui/IWorkbench;)V
  // Stack: 0, Locals: 2
  public void init(org.eclipse.ui.IWorkbench workbench);
    0  return
      Line numbers:
        [pc: 0, line: 32]
      Local variable table:
        [pc: 0, pc: 1] local: this index: 0 type: ch.weirich.templator.pages.Preferences
        [pc: 0, pc: 1] local: workbench index: 1 type: org.eclipse.ui.IWorkbench
  
  // Method descriptor #12 ()V
  // Stack: 6, Locals: 2
  protected void createFieldEditors();
      0  new org.eclipse.swt.widgets.Label [67]
      3  dup
      4  aload_0 [this]
      5  invokevirtual ch.weirich.templator.pages.Preferences.getFieldEditorParent() : org.eclipse.swt.widgets.Composite [69]
      8  bipush 64
     10  invokespecial org.eclipse.swt.widgets.Label(org.eclipse.swt.widgets.Composite, int) [73]
     13  astore_1 [info]
     14  aload_1 [info]
     15  ldc <String "Geben Sie bitte den Startbefehl für die Ausgabe des Dokuments ein.\nSetzen Sie % für den Namen des auszugebenden Dokuments"> [76]
     17  invokevirtual org.eclipse.swt.widgets.Label.setText(java.lang.String) : void [78]
     20  aload_1 [info]
     21  iconst_2
     22  iconst_1
     23  iconst_1
     24  iconst_0
     25  invokestatic ch.elexis.util.SWTHelper.getFillGridData(int, boolean, int, boolean) : org.eclipse.swt.layout.GridData [81]
     28  invokevirtual org.eclipse.swt.widgets.Label.setLayoutData(java.lang.Object) : void [87]
     31  aload_0 [this]
     32  new org.eclipse.jface.preference.StringFieldEditor [91]
     35  dup
     36  new java.lang.StringBuilder [14]
     39  dup
     40  getstatic ch.weirich.templator.pages.Preferences.PREFERENCE_BRANCH : java.lang.String [16]
     43  invokestatic java.lang.String.valueOf(java.lang.Object) : java.lang.String [18]
     46  invokespecial java.lang.StringBuilder(java.lang.String) [24]
     49  ldc <String "cmd"> [93]
     51  invokevirtual java.lang.StringBuilder.append(java.lang.String) : java.lang.StringBuilder [30]
     54  invokevirtual java.lang.StringBuilder.toString() : java.lang.String [34]
     57  ldc <String "Befehl"> [95]
     59  aload_0 [this]
     60  invokevirtual ch.weirich.templator.pages.Preferences.getFieldEditorParent() : org.eclipse.swt.widgets.Composite [69]
     63  invokespecial org.eclipse.jface.preference.StringFieldEditor(java.lang.String, java.lang.String, org.eclipse.swt.widgets.Composite) [97]
     66  invokevirtual ch.weirich.templator.pages.Preferences.addField(org.eclipse.jface.preference.FieldEditor) : void [100]
     69  aload_0 [this]
     70  new org.eclipse.jface.preference.StringFieldEditor [91]
     73  dup
     74  new java.lang.StringBuilder [14]
     77  dup
     78  getstatic ch.weirich.templator.pages.Preferences.PREFERENCE_BRANCH : java.lang.String [16]
     81  invokestatic java.lang.String.valueOf(java.lang.Object) : java.lang.String [18]
     84  invokespecial java.lang.StringBuilder(java.lang.String) [24]
     87  ldc <String "param"> [104]
     89  invokevirtual java.lang.StringBuilder.append(java.lang.String) : java.lang.StringBuilder [30]
     92  invokevirtual java.lang.StringBuilder.toString() : java.lang.String [34]
     95  ldc <String "Parameter"> [106]
     97  aload_0 [this]
     98  invokevirtual ch.weirich.templator.pages.Preferences.getFieldEditorParent() : org.eclipse.swt.widgets.Composite [69]
    101  invokespecial org.eclipse.jface.preference.StringFieldEditor(java.lang.String, java.lang.String, org.eclipse.swt.widgets.Composite) [97]
    104  invokevirtual ch.weirich.templator.pages.Preferences.addField(org.eclipse.jface.preference.FieldEditor) : void [100]
    107  return
      Line numbers:
        [pc: 0, line: 36]
        [pc: 14, line: 37]
        [pc: 20, line: 38]
        [pc: 31, line: 39]
        [pc: 59, line: 40]
        [pc: 66, line: 39]
        [pc: 69, line: 41]
        [pc: 95, line: 42]
        [pc: 104, line: 41]
        [pc: 107, line: 44]
      Local variable table:
        [pc: 0, pc: 108] local: this index: 0 type: ch.weirich.templator.pages.Preferences
        [pc: 14, pc: 108] local: info index: 1 type: org.eclipse.swt.widgets.Label
  
  // Method descriptor #12 ()V
  // Stack: 1, Locals: 1
  protected void performApply();
    0  getstatic ch.elexis.Hub.localCfg : ch.rgw.io.Settings [45]
    3  invokevirtual ch.rgw.io.Settings.flush() : void [111]
    6  return
      Line numbers:
        [pc: 0, line: 48]
        [pc: 6, line: 49]
      Local variable table:
        [pc: 0, pc: 7] local: this index: 0 type: ch.weirich.templator.pages.Preferences
}