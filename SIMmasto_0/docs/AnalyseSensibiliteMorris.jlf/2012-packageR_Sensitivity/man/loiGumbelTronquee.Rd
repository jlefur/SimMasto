\name{loiGumbelTronquee}
\alias{loiGumbelTronquee}
\alias{loiGumbelTronquee}
\title{The Truncated Gumbel distribution}
\usage{dtgumbel(x, loc = 0, scale = 1, min = -1e6, max = 1e6)

ptgumbel(q, loc = 0, scale = 1, min = -1e6, max = 1e6)

qtgumbel(p, loc = 0, scale = 1, min = -1e6, max = 1e6)

rtgumbel(n, loc = 0, scale = 1, min = -1e6, max = 1e6)}
\description{Fonctions associées à la loi de Gumbel tronquée}
\alias{dtgumbel}
\alias{ptgumbel}
\alias{qtgumbel}
\alias{rtgumbel}
\value{densités, probabilités, quantiles, échantillon aléatoire}
\arguments{\item{x}{vecteur de quantiles}
\item{q}{vecteur de quantiles}
\item{p}{vecteur de probabilités}
\item{n}{taille de l'échantillon aléatoire à générer}
\item{loc}{paramètre de position de la loi à tronquer}
\item{scale}{paramètre d'échelle de la loi à tronquer}
\item{min}{borne inférieure de la troncature}
\item{max}{borne supérieure de la troncature}
}

