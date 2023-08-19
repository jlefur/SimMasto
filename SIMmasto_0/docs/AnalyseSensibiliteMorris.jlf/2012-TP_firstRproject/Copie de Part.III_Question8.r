###############################################################################
### Partie III. question 8
###############################################################################
### Chargement des Rdata
# entrer ici le chemin vers le dossier Rdata
setwd("F:/Formation/Atelier SA/RData_new")
load("fast_7param_ss_regles.Rdata")
load("fast_7param_MPA.Rdata")
load("fast_7param_TAC.Rdata")

nameMgtStr = c("ss_regles","MPA","TAC")

library(sensitivity)
#-------------------------------------------------------------------------------
jpeg(filename = "F:/Formation/Atelier SA/Graphiques/PIIIQ8_Diagnostic.jpg",width=900,)
par(mfrow = c(2,3))
for(i in c(4,6,2,3,5,1)){ # pour chaque Y
  y = 7+i    # colonne correspondante du plan d'experience
  output = c(6,3,4,1,5,2)[i]
  # reccupere la colonne de Y dans le plan d'exp pour chaque mesure de gestion
  eval(parse(text=paste("res_ssr = fast_7param_ss_regles_Y",output,"$isis.simule[,",y,"]",sep="")))
  eval(parse(text=paste("res_mpa = fast_7param_MPA_Y",output,"$isis.simule[,",y,"]",sep="")))
  eval(parse(text=paste("res_tac = fast_7param_TAC_Y",output,"$isis.simule[,",y,"]",sep="")))

  res_mgtstr = data.frame(output = c(res_ssr,res_mpa,res_tac),
      mgtstr=c(rep("ss_regle",length(res_ssr)),rep("AMP",length(res_mpa)),rep("TAC",length(res_tac))),stringsAsFactors=F)

  boxplot(output~as.factor(mgtstr),data=res_mgtstr,main =paste("diagnostic sur Y",output,sep="")
                  ,col=c("lightPink","lightBlue","lightGreen"))
}
dev.off()