import { Component, EventEmitter, Output, inject } from '@angular/core';
import { SvgImageComponent } from '../svg-image/svg-image.component';
import { Review, ReviewService, ReviewUI } from '../../services/review.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-create-or-edit-review-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, SvgImageComponent],
  templateUrl: './create-or-edit-review-modal.component.html',
  styleUrl: './create-or-edit-review-modal.component.css',
})
export class CreateOrEditReviewModalComponent {
  private reviewService: ReviewService = inject(ReviewService);

  private _type: ModalType;
  private _review?: ReviewUI;
  private _comment: string;

  @Output()
  public submitEvent: EventEmitter<ReviewUI>;

  constructor() {
    this._type = 'CREATE';
    this.submitEvent = new EventEmitter();
    this._comment = '';
  }

  public get review() {
    return this._review;
  }

  public get comment() {
    return this._comment;
  }

  public set comment(comment: string) {
    this._comment = comment;
  }

  public get type() {
    return this._type;
  }

  public set type(type: ModalType) {
    this._type = type;
  }

  setReview(review: Review | undefined) {
    if (!review) {
      const _review: Review = {
        topicReviews: this.reviewService.populateReviewItems([]),
      };

      this._review = this.reviewService.mapToReviewUI(_review);

      return;
    }

    this._review = this.reviewService.mapToReviewUI(review);

    if (this._review.comment) {
      this._comment = this._review.comment;
    }
  }

  doRate(itemIndex: number, starIndex: number) {
    if (!this._review) {
      return;
    }

    const prevRate = this._review.topicReviews[itemIndex].stars.reduce(
      (totalizer, value) => {
        return totalizer + (value ? 1 : 0);
      },
      0
    );
    const newRate = starIndex + 1;

    if (prevRate === newRate) {
      this._review.topicReviews[itemIndex].stars = Array.from(
        { length: 5 },
        () => false
      );
      return;
    }

    const newStars = this._review.topicReviews[itemIndex].stars.map(
      (_, _starIndex) => {
        return _starIndex <= starIndex;
      }
    );

    this._review.topicReviews[itemIndex].stars = newStars;
  }

  onSubmit() {
    if (this._review) {
      this._review.comment = this._comment;
    }

    this.submitEvent.emit(this._review);
  }
}

export type ModalType = 'CREATE' | 'EDIT';
