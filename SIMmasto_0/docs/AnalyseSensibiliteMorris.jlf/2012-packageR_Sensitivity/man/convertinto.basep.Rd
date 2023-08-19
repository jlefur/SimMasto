\name{convertinto.basep}
\alias{convertinto.basep}
\title{Utilitaire plans fractionnaires: conversion base 10 vers base p...}
\usage{convertinto.basep(x, p)
}
\description{Utilitaire plans fractionnaires: conversion base 10 vers base p}
\value{matrice dont les lignes forment des nombres en base p}
\note{les coefficients sont ordonnés par puissances croissantes de p}
\arguments{\item{x}{vecteur des valeurs en base 10}
\item{p}{un nombre entier premier}
}
\examples{convertinto.basep( x=c(0:16), p=2 )
convertinto.basep( x=c(0:16), p=3 )}

