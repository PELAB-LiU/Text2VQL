import pandas as pd

def loadBuildReport(report, root=""):
    df = pd.read_csv(report)
    df["FilePath"] = df["FilePath"].str.replace(r"^\.\./", f"{root}", regex=True)
    df["Status"] = df["Status"].eq("Success")
    return df
