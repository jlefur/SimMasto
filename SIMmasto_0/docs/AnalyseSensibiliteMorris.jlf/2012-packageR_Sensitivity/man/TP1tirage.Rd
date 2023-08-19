\name{TP1tirage}
\alias{TP1tirage}
\title{Tirage uniforme dans un pav\'e de R^K...}
\usage{TP1tirage(PAV, binf, bsup, Nbclass)
}
\description{Tirage uniforme dans un pavé de R^K}
\value{vecteur à \eqn{K} éléments}
\arguments{\item{PAV}{vecteur des coordonnées entières d'un pavé codées de 1 à Nbclass}
\item{binf}{vecteur des bornes inf des gammes des facteurs}
\item{bsup}{vecteur des bornes sup des gammes des facteurs}
\item{Nbclass}{niveau de discrétisation des gammes des facteurs}
}
\examples{TP1tirage( PAV=c(1,2,3),binf=c(-10,0,100),bsup=c(10,5,600),Nbclass=5)}

