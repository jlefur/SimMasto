\name{loiGeneriqueTronquee}
\alias{loiGeneriqueTronquee}
\alias{loiGeneriqueTronquee}
\title{Fonctions g\'en\'eriques pour loi tronqu\'ee}
\usage{d.trunc.distr(x, distr, trunc.int, ...)

p.trunc.distr(q, distr, trunc.int, ...)

q.trunc.distr(p, distr, trunc.int, ...)

r.trunc.distr(n, distr, trunc.int, ...)}
\description{Fonctions génériques pour loi tronquée}
\alias{d.trunc.distr}
\alias{p.trunc.distr}
\alias{q.trunc.distr}
\alias{r.trunc.distr}
\alias{LoiGeneriqueTronquee}
\value{densités, probabilités, quantiles, échantillon aléatoire}
\arguments{\item{x}{vecteur de quantiles}
\item{q}{vecteur de quantiles}
\item{p}{vecteur de probabilités}
\item{n}{taille de l'échantillon aléatoire à générer}
\item{distr}{intitulé de la loi à tronquer}
\item{trunc.int}{bornes de la troncature}
\item{...}{paramètres de la loi à tronquer}
}
\examples{
d.trunc.distr(x=c(-2.5,-1.96,-1,0,1,1.96,2.5), distr = 'norm',
                         trunc.int = c(-2, 2), mean = 0, sd = 1)
d.trunc.distr(x=c(-2.5,-1.96,-1,0,1,1.96,2.5),
              distr = c('dnorm', 'pnorm', 'qnorm', 'rnorm'),
              trunc.int = c(-2, 2), mean = 0, sd = 1)}

