source("wwdm.R")
source("regularfractions.R")

wwdm.climates = read.table("serieclim.dat",header=T)

# fichier des caract�ristiques des facteurs: valeurs pard�faut et bornes 
wwdm.factors = data.frame(
     name=c("Eb","Eimax","K","Lmax","A","B","TI","Clim"),
     nominal=c(1.85, 0.94, 0.7, 7.5, 0.0065, 0.00205, 900, NA),
     binf=c(0.9, 0.9, 0.6, 3, 0.0035, 0.0011, 700, 1),
     bsup=c(2.8, 0.99, 0.8, 12, 0.01, 0.0025, 1100, 14),
     integer=c(rep(FALSE,7),TRUE),
     stringsAsFactors=FALSE)

 nfac = 7
   nsim = 2
   X = matrix(0, nsim, nfac)
   for(i in 1:nsim){
      for(j in 1:nfac){
              X[i,j]=runif(1,wwdm.factors$binf[j],wwdm.factors$bsup[j])
                      }
                   }
    Y=wwdm.simule(X,9)

 essai = morris(model = NULL, 4, r=3,
 list(type="oat",levels=6, grid.jump=3))


nfac=7

# etude.morris = morris(model = NULL ,
#      factors=wwdm.factors$name[1:nfac] , r = 10 ,
#      design = list(type = "oat"</SPAN>, levels = 6 , grid.jump = 3),
#      scale=F)
# X0=etude.morris$X
# for(j in 1:nfac){
#  X0[,j]=  wwdm.factors$binf[j]+ (wwdm.factors$bsup[j]-wwdm.factors$binf[j])*X0[,j]  
#                }
#  etude.morris$X=X0
#  Y0 = wwdm.simule( X0,9)
#  plot(tell(x = etude.morris, y = Y0))

etude.morris = morris(model =  wwdm.simule,
      factors=wwdm.factors$name[1:nfac] , r = 10 ,
      design = list(type = "oat", levels = 6, grid.jump = 3),
      scale=F,
      binf=wwdm.factors$binf, bsup=wwdm.factors$bsup, year=9)

par(mfrow=c(3,3),ask=T)
for(i in 1:nfac){
  plot(etude.morris$X[,i], etude.morris$y,pch=21,bg="yellow")
  title(wwdm.factors$name[i])
# lowess est un algorithme de r�gression locale et robuste
  lines( lowess(etude.morris$X[,i] , etude.morris$y), lwd=2, col="red")
  } 
 par(mfrow=c(1,1),ask=T)
 plot(etude.morris)
 IC.mu = apply(abs(etude.morris$ee),2, t.test)
 for(i in 1:nfac) print(IC.mu[[i]]$conf.int)

 sigma = sqrt(apply(etude.morris$ee,2,var))
 N = etude.morris$r 
 for(i in 1:nfac) print(c(sigma[i]*(N-1)^.5/qchisq(.975,N-1)^.5, 
                       sigma[i]*(N-1)^.5/qchisq(.025,N-1)^.5))


  plan7.2.V = regular.fraction(nfac,2,6,5)$plan
# noms des colonnes:
  colnames(plan7.2.V)=wwdm.factors$name[1:nfac]

# pour raison de codage on ajoute 1
   plan7.2.V = plan7.2.V + 1
   nrep=3
   M=NULL ;for(j in 1:nrep) M=rbind(M, plan7.2.V)
  
  Plan.rep= M
   binf = wwdm.factors$binf[1:nfac]
   bsup = wwdm.factors$bsup[1:nfac]

# on applique tirage.r sur chaque ligne de Plan.rep 
  simu = t(apply(Plan.rep, 1, tirage.r, binf, bsup, 2) )

N = nrow(simu)
Y = numeric(N)
x0 = numeric(nfac)

for(i in 1:N){
  x0 = simu[i,]
  Y[i] = sum( wwdm.model(input= x0, year=9)) 

simu.dat = data.frame(Eb =    as.factor(simu[,1]),
                      Eimax = as.factor(simu[,2]), 
                      K =    as.factor(simu[,3]), 
                      Lmax =    as.factor(simu[,4]),
                      A =    as.factor(simu[,5]),
                      B =    as.factor(simu[,6]),
                      TI =    as.factor(simu[,7]),
                      Y = Y) 

simu.aov = aov(Y ~ (Eb + Eimax + K+ Lmax + A + B + TI)^2, simu.dat)
table.aov = print(summary(simu.aov)) 

# SS = somme des carr�s,  
  SS = table.aov[[1]][2]
  neffets = nrow(SS)-1
  SS.fac=SS[1:neffets,]
  SStot = sum(SS.fac)

  vv = terms(Y ~(Eb + Eimax + K+ Lmax + A + B + TI)^2 , keep.order = F)

  vv1 = attr(vv, "factors")
  Itot = rep(NA,nfac)
  Iprinc = SS.fac[1:nfac]/SStot
    for(i in 1:nfac) Itot[i] = sum(SS.fac[vv1[i+1,]==1])/SStot
  M = rbind(Iprinc, Itot-Iprinc)
 
 barplot( M,col=c("lightblue", "blue"),
         names.arg=wwdm.factors$name[1:nfac])
 title("PLAN FRACTIONNAIRE")

plan.complet = expand.grid(Eb = 1:3, Eimax= 1:3, K= 1:3, Lmax = 1:3,A = 1:3,
                           B = 1:3, TI = 1:3, REP = 1:3)
  N = nrow(plan.complet)
  binf = wwdm.factors$binf[1:nfac]
  bsup = wwdm.factors$bsup[1:nfac]
  simu = t(apply(plan.complet[1:nfac], 1, tirage.r, binf, bsup, 3) )

  Y = numeric(N)
  for(i in 1:N){
     Y[i] = sum(wwdm.model(input= simu[i,], year=9)) 
               }

simu.dat = data.frame(Eb =    as.factor(simu[,1]),
                      Eimax = as.factor( simu[,2]), 
                      K =      as.factor(simu[,3]), 
                      Lmax =   as.factor(simu[,4]),
                      A =      as.factor(simu[,5]),
                      B =      as.factor(simu[,6]),
                      TI =     as.factor(simu[,7]),
                      Y = Y)

simu.aov = aov(Y ~ (Eb + Eimax + K+ Lmax + A + B + TI)^4, simu.dat)
table.aov = summary(simu.aov)       
SS = table.aov[[1]][2]
  neffets = nrow(SS)-1
# rem: on prend les effets du mod�le d'anova hors r�sidus
  SS.fac = SS[1:neffets,]
  SStot = sum(SS.fac)

  vv = terms(Y ~(Eb + Eimax + K+ Lmax + A + B + TI)^4 , keep.order = F)

  vv1 = attr(vv, "factors")
  Itot = rep(NA,nfac)
  Iprinc= SS.fac[1:nfac]/SStot
    for(i in 1:nfac) Itot[i] = sum(SS.fac[vv1[i+1,]==1])/SStot
  M = rbind(Iprinc,Itot-Iprinc)
 
 barplot( M,col=c("lightblue","blue"),
         names.arg=wwdm.factors$name[1:nfac])
 title("PLAN COMPLET")
