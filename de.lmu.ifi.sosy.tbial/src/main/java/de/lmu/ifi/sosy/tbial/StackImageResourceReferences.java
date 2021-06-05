package de.lmu.ifi.sosy.tbial;

import org.apache.wicket.request.resource.PackageResourceReference;

public class StackImageResourceReferences {
	
  public static PackageResourceReference cardBackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/backSide.png");

  public static PackageResourceReference bigStackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackBig.png");

  public static PackageResourceReference mediumStackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackMedium.png");

  public static PackageResourceReference smallStackImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackSmall.png");

  public static PackageResourceReference stackEmptyImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/StackEmpty.png");

  public static PackageResourceReference heapEmptyImage =
      new PackageResourceReference(PlayerAreaPanel.class, "imgs/cards/HeapEmpty.png");
}
