\name{loiNormaleTronquee}
\alias{loiNormaleTronquee}
\alias{loiNormaleTronquee}
\title{The Truncated Normal distribution}
\usage{dtnorm(x, mean = 0, sd = 1, min = -1e6, max = 1e6)

ptnorm(q, mean = 0, sd = 1, min = -1e6, max = 1e6)

qtnorm(p, mean = 0, sd = 1, min = -1e6, max = 1e6)

rtnorm(n, mean = 0, sd = 1, min = -1e6, max = 1e6)}
\description{Fonctions associées à la loi Normale tronquée}
\alias{dtnorm}
\alias{ptnorm}
\alias{qtnorm}
\alias{rtnorm}
\value{densités, probabilités, quantiles, échantillon aléatoire}
\arguments{\item{x}{vecteur de quantiles}
\item{q}{vecteur de quantiles}
\item{p}{vecteur de probabilités}
\item{n}{taille de l'échantillon aléatoire à générer}
\item{mean}{moyenne de la loi à tronquer}
\item{sd}{écart-type de la loi à tronquer}
\item{min}{borne inférieure de la troncature}
\item{max}{borne supérieure de la troncature}
}

