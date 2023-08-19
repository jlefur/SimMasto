\name{loiLogNormaleTronquee}
\alias{loiLogNormaleTronquee}
\alias{loiLogNormaleTronquee}
\title{The Truncated LogNormal distribution}
\usage{dtlnorm(x, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)

ptlnorm(q, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)

qtlnorm(p, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)

rtlnorm(n, meanlog = 0, sdlog = 1, min = -1e6, max = 1e6)}
\description{Fonctions associées à la loi LogNormale tronquée}
\alias{dtlnorm}
\alias{ptlnorm}
\alias{qtlnorm}
\alias{rtlnorm}
\value{densités, probabilités, quantiles, échantillon aléatoire}
\arguments{\item{x}{vecteur de quantiles}
\item{q}{vecteur de quantiles}
\item{p}{vecteur de probabilités}
\item{n}{taille de l'échantillon aléatoire à générer}
\item{meanlog}{moyenne de la loi à tronquer}
\item{sdlog}{écart-type de la loi à tronquer}
\item{min}{borne inférieure de la troncature}
\item{max}{borne supérieure de la troncature}
}

