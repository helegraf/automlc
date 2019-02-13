package automlc.experiments.gaautomlc;


import org.aeonbits.owner.Config.LoadPolicy;
import org.aeonbits.owner.Config.LoadType;
import org.aeonbits.owner.Config.Sources;

import jaicore.ml.experiments.IMultiClassClassificationExperimentConfig;

@LoadPolicy(LoadType.MERGE)
@Sources({ "file:conf/ml2planAutoExperimenter.properties", "file:conf/ml2planAutodatabase.properties" })
public interface GAAutoMLCExperimenterConfig extends IMultiClassClassificationExperimentConfig {

}