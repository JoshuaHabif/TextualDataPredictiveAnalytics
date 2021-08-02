
import pandas as pd
import plotly.express as px


df = pd.read_csv(input("Path to pca_tfidf.csv:"))

fig_one = px.scatter_3d(df, x='PCA1', y='PCA2', z='PCA3', text="Text",
     color="Articles About", title="Scatter plot of documents by folder topic")

fig_two = px.scatter_3d(df, x='PCA1', y='PCA2', z='PCA3', text="Text",
     color="Predicted Topic", title="Scatter plot of predicted document topic")

fig_one.write_html(input("Path to save fig 1"))

fig_two.write_html(input("Path to save fig 2"))




