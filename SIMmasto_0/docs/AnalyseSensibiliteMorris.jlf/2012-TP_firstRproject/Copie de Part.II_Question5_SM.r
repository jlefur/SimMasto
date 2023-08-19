###############################################################################
### Partie II. question 5
###############################################################################
### Chargement des Rdata
# entrer ici le chemin vers le dossier Rdata
setwd("G:/DossiersASynchroniser/AnalysesSensibilite/AtelierAFH/Rdata/")
# que fait la fonction ?
library(sensitivity)
?morris

###########
lesPlans <- c("morris_7param_r20","morris_7param_r6")
for (plan in lesPlans){
  for(i in 1:6)
  {
  
    print(paste("Pour ",i))
    load(paste(plan,"_ss_regles_Y",i,".Rdata",sep=""))
    load(paste(plan,"_MPA_Y",i,".Rdata",sep=""))
    if (plan == "morris_7param_r20") {
     load(paste(plan,"_Y",i,".Rdata",sep=""))
    } else { load(paste(plan,"_TAC_Y",i,".Rdata",sep=""))}
    
    # on les renomme pour chaque strategie de gestion
    ssr = eval(parse(text=paste(plan,"_ss_regles_Y",i,sep="")))
    amp = eval(parse(text=paste(plan,"_MPA_Y",i,sep="")))
    if (plan == "morris_7param_r20") {
     tac = eval(parse(text=paste(plan,"_Y",i,sep="")))
    }  else { tac = eval(parse(text=paste(plan,"_TAC_Y",i,sep="")))}
  
  
    jpeg(filename = paste("G:/DossiersASynchroniser/AnalysesSensibilite/AtelierAFH/Graphiques/PIIQ5_",plan,"_Y",i,".jpg",sep=""),width=900)
    par(mfrow=c(1,3),mar=c(1,1,1,1)) 
    # pour chaque mesure de gestion
    res_ssr = ssr$analysis_result
    print(res_ssr)
    plot(res_ssr)
    title(main = paste(plan," SS Regle Y",i,sep=""))
     
    res_amp = amp$analysis_result
    print(res_amp)
    plot(res_amp)
    title(main = paste(plan," AMP Y",i,sep=""))
      
    res_tac = tac$analysis_result
    print(res_tac)
    plot(res_tac)
    title(main = paste(plan," TAC Y",i,sep=""))
    dev.off()
    }
  }
