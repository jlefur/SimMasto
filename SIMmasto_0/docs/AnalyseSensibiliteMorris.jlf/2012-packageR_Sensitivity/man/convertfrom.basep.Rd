\name{convertfrom.basep}
\alias{convertfrom.basep}
\title{Utilitaire plans fractionnaires: conversion base p vers base 10...}
\usage{convertfrom.basep(x, p)
}
\description{Utilitaire plans fractionnaires: conversion base p vers base 10}
\value{vecteur des valeurs en base 10}
\note{les coefficients sont supposés ordonnés par puissances croissantes de p}
\arguments{\item{x}{matrice dont les lignes forment des nombres en base p}
\item{p}{un nombre entier premier}
}
\examples{conv.into2 <- convertinto.basep( x=c(0:16), p=2 )
convertfrom.basep(x=conv.into2, p=2)}

