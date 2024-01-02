import {
  ApplicationRef,
  ComponentRef,
  EnvironmentInjector,
  Injectable,
  createComponent,
  inject,
} from '@angular/core';
import { Observable } from 'rxjs';

import { ModalComponent } from '../components/modal/modal.component';
import { CreateOccupationModalContentComponent } from '../components/create-occupation-modal-content/create-occupation-modal-content.component';
import {
  CreateOrEditOrderItemModalComponent,
  CreateOrEditOrderItemOutputData,
} from '../components/create-or-edit-order-item-modal/create-or-edit-order-item-modal.component';
import {
  EditOrderItemAction,
  EditOrderItemModalComponent,
} from '../components/edit-order-item-modal/edit-order-item-modal.component';

@Injectable({
  providedIn: 'root',
})
export class ModalService {
  private createOccupationNotifier?: Observable<number>;
  private createOrEditOrderItemNotifier?: Observable<CreateOrEditOrderItemOutputData>;
  private editOrderItemNotifier?: Observable<EditOrderItemAction>;

  private appRef: ApplicationRef = inject(ApplicationRef);
  private injector: EnvironmentInjector = inject(EnvironmentInjector);

  public newModalComponent!: ComponentRef<ModalComponent>;

  openCreateOccupationModal() {
    const newComponent = createComponent(
      CreateOccupationModalContentComponent,
      {
        environmentInjector: this.injector,
      }
    );

    this.createOccupationNotifier =
      newComponent.instance.amountPeopleEventEmitter.asObservable();

    this.newModalComponent = createComponent(ModalComponent, {
      environmentInjector: this.injector,
      projectableNodes: [[newComponent.location.nativeElement]],
    });

    this.newModalComponent.hostView.detectChanges();

    document.body.appendChild(this.newModalComponent.location.nativeElement);

    this.appRef.attachView(newComponent.hostView);
    this.appRef.attachView(this.newModalComponent.hostView);

    return this.createOccupationNotifier;
  }

  openCreateOrderItemModal() {
    const newComponent = createComponent(CreateOrEditOrderItemModalComponent, {
      environmentInjector: this.injector,
    });

    this.createOrEditOrderItemNotifier =
      newComponent.instance.submitEvent.asObservable();

    this.newModalComponent = createComponent(ModalComponent, {
      environmentInjector: this.injector,
      projectableNodes: [[newComponent.location.nativeElement]],
    });

    this.newModalComponent.hostView.detectChanges();

    document.body.appendChild(this.newModalComponent.location.nativeElement);

    this.appRef.attachView(newComponent.hostView);
    this.appRef.attachView(this.newModalComponent.hostView);

    return this.createOrEditOrderItemNotifier;
  }

  openEditOrderItemModal(showEditButton: boolean = true) {
    const newComponent = createComponent(EditOrderItemModalComponent, {
      environmentInjector: this.injector,
    });

    if (!showEditButton) {
      newComponent.instance.hideEditButton();
    }

    this.editOrderItemNotifier =
      newComponent.instance.editOrderItemActionEvent.asObservable();

    this.newModalComponent = createComponent(ModalComponent, {
      environmentInjector: this.injector,
      projectableNodes: [[newComponent.location.nativeElement]],
    });

    this.newModalComponent.hostView.detectChanges();

    document.body.appendChild(this.newModalComponent.location.nativeElement);

    this.appRef.attachView(newComponent.hostView);
    this.appRef.attachView(this.newModalComponent.hostView);

    return this.editOrderItemNotifier;
  }

  openEditOrderItemEditActionModal(
    itemAmount: number,
    itemObservation: string | null
  ) {
    const newComponent = createComponent(CreateOrEditOrderItemModalComponent, {
      environmentInjector: this.injector,
    });

    this.createOrEditOrderItemNotifier =
      newComponent.instance.submitEvent.asObservable();
    newComponent.instance.itemAmount = itemAmount;
    newComponent.instance.itemObservation = itemObservation;
    newComponent.instance.type = 'EDIT';

    this.newModalComponent = createComponent(ModalComponent, {
      environmentInjector: this.injector,
      projectableNodes: [[newComponent.location.nativeElement]],
    });

    this.newModalComponent.hostView.detectChanges();

    document.body.appendChild(this.newModalComponent.location.nativeElement);

    this.appRef.attachView(newComponent.hostView);
    this.appRef.attachView(this.newModalComponent.hostView);

    return this.createOrEditOrderItemNotifier;
  }

  closeModal() {
    this.newModalComponent.destroy();
  }
}
