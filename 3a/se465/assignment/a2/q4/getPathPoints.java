/* Excerpt from SweetHome3D, 
 * class com.eteks.sweethome3d.viewcontroller.PlanController. */

/* Author info:
 * Sweet Home 3D, Copyright (c) 2006 Emmanuel PUYBARET / eTeks <info@eteks.com>
 * Licensed under GPL v2+.
 */

/**
 * Returns the points of a general path which contains only one path.
 */
private float [][] getPathPoints(GeneralPath path, 
                                 boolean removeAlignedPoints) {
  List<float []> pathPoints = new ArrayList<float[]>();
  float [] previousPathPoint = null;
  for (PathIterator it = path.getPathIterator(null); !it.isDone(); ) {
    float [] pathPoint = new float[2];
    if (it.currentSegment(pathPoint) != PathIterator.SEG_CLOSE
        && (previousPathPoint == null
            || !Arrays.equals(pathPoint, previousPathPoint))) {
      boolean replacePoint = false;
      if (removeAlignedPoints
          && pathPoints.size() > 1) {
        // Check if pathPoint is aligned with the last line added to pathPoints
        float [] lastLineStartPoint = pathPoints.get(pathPoints.size() - 2);
        float [] lastLineEndPoint = previousPathPoint;
        replacePoint = Line2D.ptLineDistSq(lastLineStartPoint [0], lastLineStartPoint [1], 
            lastLineEndPoint [0], lastLineEndPoint [1], 
            pathPoint [0], pathPoint [1]) < 0.0001;
      } 
      if (replacePoint) {
        pathPoints.set(pathPoints.size() - 1, pathPoint);
      } else {
        pathPoints.add(pathPoint);
      }
      previousPathPoint = pathPoint;
    }
    it.next();
  }      
  
  // Remove last point if it's equal to first point
  if (pathPoints.size() > 1
      && Arrays.equals(pathPoints.get(0), pathPoints.get(pathPoints.size() - 1))) {
    pathPoints.remove(pathPoints.size() - 1);
  }
  
  return pathPoints.toArray(new float [pathPoints.size()][]);
}
