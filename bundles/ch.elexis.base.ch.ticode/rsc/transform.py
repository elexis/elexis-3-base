import pandas as pd
import json

# Load the Excel file
file_path = 'Anhang_H_Tessiner_Code_MTK_Erweiterung_-_Kopie_RS.xlsx'
df = pd.read_excel(file_path, engine='openpyxl')

# Clean up the column names and drop unnecessary rows
df.columns = df.iloc[1]
df = df.drop([0, 1]).reset_index(drop=True)

# Create the FHIR CodeSystem structure
fhir_code_system = {
    "resourceType": "CodeSystem",
    "url": "http://elexis.info/CodeSystem/TI-Code",
    "version": "1.0.0",
    "name": "TI-Code",
    "title": "Tessiner Code",
    "status": "active",
    "date": "2026-09-02",
    "publisher": "elexis.info",
    "description": "Tessiner Code System including MTK extension for diagnosis codes.",
    "purpose": "This code system is used for encoding diagnosis in the Tessiner system, including MTK extensions.",
    "content": "complete",
    "concept": []
}


# Track the last one-character code to use as a parent
last_parent_code = None

# Populate the 'concept' array with properties for parent relationships
for _, row in df.iterrows():
    code = row['Hauptcode']

    # If the code is one character, it's a parent concept
    if len(code) == 1:
        concept = {
            "code": code,
            "display": row['Bezeichnung Deutsch'],
            "designation": [
                {
                    "language": "fr",
                    "value": row['Bezeichnung Französisch']
                },
                {
                    "language": "it",
                    "value": row['Bezeichnung italienisch']
                }
            ]
        }
        fhir_code_system["concept"].append(concept)
        last_parent_code = code

    # If the code is longer, it's a child of the last one-character code
    else:
        concept = {
            "code": code,
            "display": row['Bezeichnung Deutsch'],
            "designation": [
                {
                    "language": "fr",
                    "value": row['Bezeichnung Französisch']
                },
                {
                    "language": "it",
                    "value": row['Bezeichnung italienisch']
                }
            ]
        }

        # Add property for parent if a parent exists
        if last_parent_code:
            concept["property"] = [
                {
                    "code": "parent",
                    "valueCode": last_parent_code
                }
            ]

        fhir_code_system["concept"].append(concept)

# Save the FHIR CodeSystem JSON to a file
file_output_path = 'tessiner_mtk_extension_code_system.json'
with open(file_output_path, 'w', encoding='utf-8') as f:
    json.dump(fhir_code_system, f, indent=2, ensure_ascii=False)

print(f"FHIR CodeSystem JSON saved to {file_output_path}")