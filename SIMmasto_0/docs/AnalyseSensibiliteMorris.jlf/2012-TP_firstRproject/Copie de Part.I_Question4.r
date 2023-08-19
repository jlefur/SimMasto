###############################################################################
### Partie I. question 4
###############################################################################
### Chargement des Rdata
# entrer ici le chemin vers le dossier Rdata
setwd("F:/Formation/Atelier SA/RData_new")
load("random_ss_regles_152params.Rdata")
load("random_MPA_152params.Rdata")
load("random_TAC_152params.Rdata")

#-------------------------------------------------------------------------------
#Sans regle
nameMgtStr = "ss_regle"

### Y1
Var = "Y1"
NomFacteur <-random_ss_regles_152params_Y1$isis.factors[,1]
table.aov=random_ss_regles_152params_Y1$analysis_result
### Y2
Var = "Y2"
NomFacteur <-random_ss_regles_152params_Y2$isis.factors[,1]
table.aov=random_ss_regles_152params_Y2$analysis_result
### Y3
Var = "Y3"
NomFacteur <-random_ss_regles_152params_Y3$isis.factors[,1]
table.aov=random_ss_regles_152params_Y3$analysis_result
### Y4
Var = "Y4"
NomFacteur <-random_ss_regles_152params_Y4$isis.factors[,1]
table.aov=random_ss_regles_152params_Y4$analysis_result
### Y5
Var = "Y5"
NomFacteur <-random_ss_regles_152params_Y5$isis.factors[,1]
table.aov=random_ss_regles_152params_Y5$analysis_result
### Y6
Var = "Y6"
NomFacteur <-random_ss_regles_152params_Y6$isis.factors[,1]
table.aov=random_ss_regles_152params_Y6$analysis_result

#AMP
nameMgtStr = "AMP"
### Y1
Var = "Y1"
NomFacteur <-random_MPA_152params_Y1$isis.factors[,1]
table.aov=random_MPA_152params_Y1$analysis_result
### Y2
Var = "Y2"
NomFacteur <-random_MPA_152params_Y2$isis.factors[,1]
table.aov=random_MPA_152params_Y2$analysis_result
### Y3
Var = "Y3"
NomFacteur <-random_MPA_152params_Y3$isis.factors[,1]
table.aov=random_MPA_152params_Y3$analysis_result
### Y4
Var = "Y4"
NomFacteur <-random_MPA_152params_Y4$isis.factors[,1]
table.aov=random_MPA_152params_Y4$analysis_result
### Y5
Var = "Y5"
NomFacteur <-random_MPA_152params_Y5$isis.factors[,1]
table.aov=random_MPA_152params_Y5$analysis_result
### Y6
Var = "Y6"
NomFacteur <-random_MPA_152params_Y6$isis.factors[,1]
table.aov=random_MPA_152params_Y6$analysis_result

#TAC
nameMgtStr = "TAC"
### Y1
Var = "Y1"
NomFacteur <-random_TAC_152params_Y1$isis.factors[,1]
table.aov=random_TAC_152params_Y1$analysis_result
### Y2
Var = "Y2"
NomFacteur <-random_TAC_152params_Y2$isis.factors[,1]
table.aov=random_TAC_152params_Y2$analysis_result
### Y3
Var = "Y3"
NomFacteur <-random_TAC_152params_Y3$isis.factors[,1]
table.aov=random_TAC_152params_Y3$analysis_result
### Y4
Var = "Y4"
NomFacteur <-random_TAC_152params_Y4$isis.factors[,1]
table.aov=random_TAC_152params_Y4$analysis_result
### Y5
Var = "Y5"
NomFacteur <-random_TAC_152params_Y5$isis.factors[,1]
table.aov=random_TAC_152params_Y5$analysis_result
### Y6
Var = "Y6"
NomFacteur <-random_TAC_152params_Y6$isis.factors[,1]
table.aov=random_TAC_152params_Y6$analysis_result




### Indices de sensibilité
Iprinc = table.aov[[2]][1:152]
jpeg(filename = paste("F:/Formation/Atelier SA/Graphiques/PIQ4_",nameMgtStr,Var,".jpg",sep=""))
barplot(Iprinc,main=paste("LHS Random ",Var," ",nameMgtStr),las=2)
dev.off()
### fixe un seuil de significativité
names(Iprinc) = gsub("nephrops.","",NomFacteur)
Iprinc2 = Iprinc[Iprinc > 0.01]
jpeg(filename = paste("F:/Formation/Atelier SA/Graphiques/PIQ4_",nameMgtStr,Var,"Signif.jpg",sep=""))
par(mar=c(15,2,2,2))
barplot(Iprinc2,main=paste("LHS Random ",Var," ",nameMgtStr," significant"),las=3)
dev.off()
