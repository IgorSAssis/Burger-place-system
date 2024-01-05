import { Component, Input, OnInit, inject } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';

import { ModalService } from '../../services/modal.service';
import {
  CreateOrderItemsDTO,
  Occupation,
  OccupationService,
  UpdateOrderItemDTO,
} from '../../services/occupation.service';
import {
  Category,
  CategoryType,
  Product,
  ProductService,
} from '../../services/product.service';

import { SvgImageComponent } from '../../components/svg-image/svg-image.component';
import { BreadcrumbComponent } from '../../components/breadcrumb/breadcrumb.component';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import {
  OrderItem,
  OrderItemStatus,
  OrderItemStatusType,
} from '../../services/order-item.service';
import {
  CreateReviewDTO,
  Review,
  ReviewService,
} from '../../services/review.service';

@Component({
  selector: 'app-customer',
  standalone: true,
  imports: [
    CommonModule,
    SvgImageComponent,
    BreadcrumbComponent,
    PaginationComponent,
  ],
  templateUrl: './customer.component.html',
  styleUrl: './customer.component.css',
})
export class CustomerComponent implements OnInit {
  public readonly IMG_BASE_URL = 'https://img.freepik.com/fotos-gratis/';

  private productService: ProductService = inject(ProductService);
  private occupationService: OccupationService = inject(OccupationService);
  private modalService: ModalService = inject(ModalService);
  private reviewService: ReviewService = inject(ReviewService);
  private router: Router = inject(Router);

  private _expandedItems: boolean[];
  private _activeMenu: CategoryType;
  private _products: Product[];
  private readonly _availableCategories: [string, Category][];
  private _totalPages: number;
  private _currentPage: number;

  @Input()
  private occupationId?: number;

  private _occupation?: Occupation;
  private _review?: Review;

  constructor() {
    this._currentPage = 0;
    this._totalPages = 0;
    this._expandedItems = [false, false];
    this._activeMenu = 'BURGER';
    this._availableCategories = Object.entries(Category);
    this._products = [];
  }

  ngOnInit(): void {
    this.fetchOccupation();
    this.fetchProducts();
    this.fetchReview();
  }

  public get expandedItems() {
    return this._expandedItems;
  }

  public get availableCategories() {
    return this._availableCategories;
  }

  public get products() {
    return this._products;
  }

  public get totalPages() {
    return this._totalPages;
  }

  getOccupationTotalCost() {
    return this._occupation?.orderItems
      .filter((item) => item.status !== 'CANCELADO')
      .reduce((accumulator, item) => {
        return accumulator + item.itemValue * item.amount;
      }, 0);
  }

  getHistoryOrderItems() {
    if (!this._occupation) {
      return [];
    }

    return this._occupation?.orderItems
      .filter(
        (item) => item.status === 'ENTREGUE' || item.status === 'CANCELADO'
      )
      .sort((oit1, oit2) => oit1.id - oit2.id);
  }

  getInProgressOrderItems() {
    if (!this._occupation) {
      return [];
    }

    return this._occupation?.orderItems
      .filter(
        (item) => item.status !== 'ENTREGUE' && item.status !== 'CANCELADO'
      )
      .sort((oit1, oit2) => oit1.id - oit2.id);
  }

  getOrderItemStatusDescription(status: OrderItemStatusType) {
    return OrderItemStatus[status];
  }

  onSelectProduct(product: Product) {
    this.modalService.openCreateOrderItemModal().subscribe({
      next: (modalData) => {
        this.addOrderItems(
          product.id,
          modalData.itemAmount,
          modalData.itemObservation
        );
      },
    });
  }

  onSelectOrderItem(orderItem: OrderItem) {
    this.modalService
      .openEditOrderItemModal(orderItem.status !== 'PRONTO')
      .subscribe({
        next: (action) => {
          if (action === 'CANCEL') {
            this.cancelOrderItem(orderItem.id, () => {
              alert('Item cancelado');
              this.fetchOccupation();
              this.modalService.closeModal();
            });
          } else {
            this.modalService.closeModal();
            this.modalService
              .openEditOrderItemEditActionModal(
                orderItem.amount,
                orderItem.observation
              )
              .subscribe({
                next: (newOrderItemData) => {
                  this.updateOrderItem(
                    orderItem.id,
                    newOrderItemData.itemAmount,
                    newOrderItemData.itemObservation,
                    () => {
                      alert('Item atualizado com sucesso');
                      this.fetchOccupation();
                      this.modalService.closeModal();
                    }
                  );
                },
              });
          }
        },
      });
  }

  changeCurrentPage(page: number) {
    this._currentPage = page;
    this.fetchProducts();
  }

  toggleExpanded(index: number) {
    this._expandedItems = this._expandedItems.map((expanded, _index) => {
      return _index === index ? !expanded : false;
    });
  }

  changeActiveMenu(category: string) {
    if (category in Category) {
      this._activeMenu = category as CategoryType;
      this._currentPage = 0;
      this.fetchProducts();
    }
  }

  isMenuActive(categoryType: string) {
    return this._activeMenu === categoryType;
  }

  onReview() {
    this.fetchReview();
    console.log('Fetched reviews');
    console.log(this._review);

    this.modalService.openCreateOrEditReviewModal(this._review).subscribe({
      next: (data) => {
        const mappedReview = this.reviewService.mapToReview(data);
        console.log("mappedReview");
        console.log(mappedReview);

        if (mappedReview.topicReviews.filter(item => item.grade > 0).length === 0) {
          alert(
            'É necessário realizar alguma avaliação para concluir a operação'
          );
          return;
        }

        if (typeof this._review?.id === 'undefined') {
          this.createReview(mappedReview);
          this.modalService.closeModal();
          return;
        }

        const itemsToCreate = mappedReview.topicReviews.filter(
          (item) => typeof item.id === 'undefined' && item.grade !== 0
        );
        const itemsToUpdate = mappedReview.topicReviews.filter(
          (item) => typeof item.id !== 'undefined' && item.grade !== 0
        );
        const itemsToDelete = mappedReview.topicReviews.filter(
          (item) => typeof item.id !== 'undefined' && item.grade === 0
        );

        itemsToDelete.forEach((item) => {
          this.reviewService.deleteReviewTopic(item.id as number);
        });

        itemsToUpdate.forEach((item) => {
          this.reviewService.updateReviewTopic(item.id as number, {
            grade: item.grade,
          });
        });

        itemsToCreate.forEach((item) => {
          this.reviewService.createReviewTopic({
            reviewId: this._review!.id as number,
            category: item.category,
            grade: item.grade,
          });
        });

        if (mappedReview.comment) {
          this.reviewService.updateReview(mappedReview.id as number, mappedReview.comment);
        }

        this._review = mappedReview;
        this.modalService.closeModal();
      },
    });
  }

  private fetchProducts() {
    this.productService
      .fetchProducts({
        category: this._activeMenu,
        page: this._currentPage,
      })
      .subscribe({
        next: (data) => {
          this._products = data.products;
          this._totalPages = data.totalPages;
        },
        error: (error) => console.error(error),
        complete: () => console.log('Successfull'),
      });
  }

  private fetchOccupation() {
    if (!this.occupationId) {
      this.router.navigate(['/']);
      return;
    }

    this.occupationService.fetchOccupationById(this.occupationId).subscribe({
      next: (data) => {
        this._occupation = data;
      },
      error: () => {
        alert('Ocupação inexistente');
        this.router.navigate(['/']);
      },
      complete: () => {
        console.log('Successfull');
      },
    });
  }

  private fetchReview() {
    if (!this.occupationId) {
      return;
    }

    this.reviewService.fetchReview(this.occupationId).subscribe({
      next: (data) => {
        console.log('Data');
        console.log(data);
        this._review = {
          id: data.id,
          comment: data.comment,
          topicReviews: data.topicReviews,
        };
      },
      error: (error) => console.error(error),
      complete: () => console.log('Successfull'),
    });
  }

  private addOrderItems(
    productId: number,
    amount: number,
    observation: string | null
  ) {
    if (!this.occupationId) {
      alert('ID da ocupação inválido');
      return;
    }

    const createOrderItemsDTO: CreateOrderItemsDTO = {
      orderItems: [{ productId, amount, observation }],
    };

    this.occupationService
      .addOrderItems(this.occupationId, createOrderItemsDTO)
      .subscribe({
        next: () => {
          alert('Itens adicionados com sucesso');
          this.fetchOccupation();
          this.modalService.closeModal();
        },
        error: (error) => console.error(error),
        complete: () => console.log('Successfull'),
      });
  }

  private cancelOrderItem(itemId: number, callback: () => void) {
    if (!this.occupationId) {
      alert('ID da ocupação inválido');
      return;
    }

    this.occupationService
      .cancelOrderItem(this.occupationId, itemId)
      .subscribe({
        next: () => callback(),
        error: (error) => console.error(error),
        complete: () => console.log('Successfull'),
      });
  }

  private updateOrderItem(
    itemId: number,
    newItemAmount: number,
    newItemObservation: string | null,
    callback: () => void
  ) {
    if (!this.occupationId) {
      alert('ID da ocupação inválido');
      return;
    }

    const updateOrderItemDTO: UpdateOrderItemDTO = {
      amount: newItemAmount,
      observation: newItemObservation,
    };

    this.occupationService
      .updateOrderItem(this.occupationId, itemId, updateOrderItemDTO)
      .subscribe({
        next: () => callback(),
        error: (error) => console.error(error),
      });
  }

  private createReview(review: Review) {
    if (!this.occupationId) {
      return;
    }

    const createReviewDTO: CreateReviewDTO = {
      comment: review.comment,
      occupationId: this.occupationId,
      items: review.topicReviews.map((reviewItem) => {
        return {
          category: reviewItem.category,
          grade: reviewItem.grade,
        };
      }),
    };
    console.log('createReviewDTO');

    console.log(createReviewDTO);

    this.reviewService.createReview(createReviewDTO).subscribe({
      next: (data) => {
        this._review = data;
        console.log(this._review);
      },
    });
  }
}
