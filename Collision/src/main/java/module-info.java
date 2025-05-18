import dk.sdu.mmmi.cbse.collisionsystem.CollisionSystemHandler;
import dk.sdu.mmmi.cbse.common.services.IPostEntityProcessingService;

module Collision {
    requires Common;   
    provides IPostEntityProcessingService with CollisionSystemHandler;
}